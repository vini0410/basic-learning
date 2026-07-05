# AWS — Infra para o Proxy no ECS com Observabilidade Separada

## Visão Geral da Arquitetura

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              AWS VPC (10.0.0.0/16)                          │
│                                                                             │
│  ┌──────────────────────────────────────┐  ┌───────────────────────────┐    │
│  │         Subnet Pública               │  │    Subnet Privada         │    │
│  │                                      │  │                           │    │
│  │  ┌──────────────────────────────┐    │  │  ┌─────────────────────┐  │    │
│  │  │   ECS Cluster (Fargate)      │    │  │  │  EC2 / ECS (obs)    │  │    │
│  │  │                              │    │  │  │                     │  │    │
│  │  │  ┌────────────────────────┐  │    │  │  │  ┌───────────────┐  │  │    │
│  │  │  │  Task: proxy           │  │    │  │  │  │ otel-collector│  │  │    │
│  │  │  │  ┌──────────────────┐  │  │    │  │  │  │  (gateway)    │  │  │    │
│  │  │  │  │  OpenResty       │  │  │    │  │  │  │  :4317 gRPC   │  │  │    │
│  │  │  │  │  :8081           │  │  │    │  │  │  └───────────────┘  │  │    │
│  │  │  │  └──────────────────┘  │  │    │  │  │  ┌───────────────┐  │  │    │
│  │  │  │  ┌──────────────────┐  │  │    │  │  │  │  prometheus   │  │  │    │
│  │  │  │  │ otel-col-agent   │──┼──┼────┼──┼──┼─►│  :9090        │  │  │    │
│  │  │  │  │  (sidecar)       │  │  │    │  │  │  └───────────────┘  │  │    │
│  │  │  │  └──────────────────┘  │  │    │  │  │  ┌───────────────┐  │  │    │
│  │  │  └────────────────────────┘  │    │  │  │  │  grafana      │  │  │    │
│  │  │                              │    │  │  │  │  :3000        │  │  │    │
│  │  └──────────────────────────────┘    │  │  │  └───────────────┘  │  │    │
│  │                                      │  │  └─────────────────────┘  │    │
│  └──────────────────────────────────────┘  └───────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Componentes e onde cada um roda

| Componente            | Onde roda                | Motivo                                                                 |
|-----------------------|--------------------------|------------------------------------------------------------------------|
| **OpenResty (proxy)** | ECS Fargate Task         | Escala horizontalmente, sem gerenciar servidor                         |
| **otel-col-agent**    | Sidecar na mesma ECS Task| Acesso local ao `nginx_status` via `localhost`, sem expor porta        |
| **otel-col-gateway**  | Servidor separado (EC2)  | Centraliza telemetria de múltiplos agentes/serviços                    |
| **Prometheus**        | Servidor separado (EC2)  | Raspa o gateway; dados persistentes em volume EBS                      |
| **Grafana**           | Servidor separado (EC2)  | Dashboards; acesso interno via SSM Session Manager ou VPN              |

---

## Parte 1 — Servidor de Observabilidade (EC2)

O Grafana, Prometheus e o OTel Collector Gateway rodam em um único EC2 na **subnet privada** — sem IP público.

### 1.1 Infraestrutura do servidor

```hcl
# terraform/observability_server.tf (referência)

resource "aws_instance" "observability" {
  ami           = "ami-0c55b159cbfafe1f0"  # Amazon Linux 2
  instance_type = "t3.small"
  subnet_id     = aws_subnet.private.id

  # Sem IP público — acesso só via SSM
  associate_public_ip_address = false

  iam_instance_profile = aws_iam_instance_profile.ssm_profile.name

  vpc_security_group_ids = [aws_security_group.observability.id]

  tags = { Name = "observability-server" }
}
```

### 1.2 Security Group do servidor de observabilidade

```hcl
resource "aws_security_group" "observability" {
  name   = "observability-sg"
  vpc_id = aws_vpc.main.id

  # Recebe spans/métricas OTLP gRPC vindos do agente no ECS
  ingress {
    from_port       = 4317
    to_port         = 4317
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]  # só o SG das tasks ECS
  }

  # Grafana — acessível via SSM Port Forwarding ou VPN interna
  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/8"]  # apenas VPC/VPN
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
```

### 1.3 Docker Compose no servidor de observabilidade

Crie `/opt/observability/docker-compose.yml` no EC2:

```yaml
# /opt/observability/docker-compose.yml
services:

  otel-collector-gateway:
    image: otel/opentelemetry-collector-contrib:latest
    container_name: otel-collector-gateway
    command: ["--config=/etc/otelcol-contrib/config.yaml"]
    volumes:
      - ./otel-collector-gateway.yaml:/etc/otelcol-contrib/config.yaml
    ports:
      - "4317:4317"   # OTLP gRPC — recebe do agente no ECS
      - "8889:8889"   # Prometheus scrape endpoint
    restart: unless-stopped

  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    ports:
      - "9090:9090"
    depends_on:
      - otel-collector-gateway
    restart: unless-stopped

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_ADMIN_PASSWORD}
      - GF_USERS_ALLOW_SIGN_UP=false
      - GF_SERVER_ROOT_URL=http://localhost:3000  # ou seu domínio interno
    volumes:
      - ./grafana/provisioning:/etc/grafana/provisioning
      - grafana-data:/var/lib/grafana
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
    restart: unless-stopped

volumes:
  prometheus-data:
  grafana-data:
```

### 1.4 OTel Collector Gateway no servidor

Este arquivo substitui o `otel-collector-gateway.yaml` local — sem mudança de conteúdo significativa, apenas garanta que o endpoint aceite conexões externas:

```yaml
# /opt/observability/otel-collector-gateway.yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317   # aceita conexões de qualquer origem autorizada pelo SG

processors:
  batch:

exporters:
  prometheus:
    endpoint: 0.0.0.0:8889       # Prometheus raspa aqui
    resource_to_telemetry_conversion:
      enabled: true
  debug:
    verbosity: basic

service:
  pipelines:
    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [prometheus, debug]
```

### 1.5 Prometheus apontando para o gateway local

```yaml
# /opt/observability/prometheus.yml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'otel-collector-gateway'
    static_configs:
      - targets: ['otel-collector-gateway:8889']  # nome do serviço no compose local
```

---

## Parte 2 — Proxy no ECS (Fargate)

O proxy roda como uma **ECS Task com dois containers** (Task Definition multi-container):

1. `proxy` — OpenResty
2. `otel-agent` — OTel Collector Agent como **sidecar**

### 2.1 Por que o agente como sidecar?

O `otel-collector-agent` precisa acessar o `nginx_status` do OpenResty. No ECS Fargate, containers da **mesma Task** compartilham a interface de rede — logo o agente acessa o proxy em `localhost:8081`, sem necessidade de expor a porta para fora.

```
+-------------------------------------------------+
|            ECS Task (Fargate)                   |
|                                                 |
|  +-------------------+  +--------------------+  |
|  |  proxy (OpenResty)|  |  otel-agent        |  |
|  |  :8081            |  |  (sidecar)         |  |
|  |                   |  |                    |  |
|  |  /nginx_status ---+--► localhost:8081     |  |
|  +-------------------+  +---------+----------+  |
|                                   |              |
|                       OTLP gRPC   |              |
+-----------------------------------+--------------+
                                    |
                       10.0.x.x:4317 (servidor obs)
```

### 2.2 OTel Agent Config (ajuste do endpoint)

A única mudança em relação ao `otel-collector-agent.yaml` local é o **endpoint do exporter** — troque o nome Docker pelo IP/DNS privado do servidor de observabilidade:

```yaml
# aws/otel-collector-agent.yaml
receivers:
  nginx:
    # localhost porque agent e proxy compartilham a rede da Task
    endpoint: http://localhost:8081/nginx_status
    collection_interval: 10s

  prometheus:
    config:
      scrape_configs:
        - job_name: 'openresty-custom'
          scrape_interval: 10s
          static_configs:
            - targets: ['localhost:8081']  # localhost, não nome Docker

processors:
  batch:
  resource/nginx:
    attributes:
      - key: service.name
        value: "proxy-openresty"
        action: upsert

exporters:
  otlp:
    # IP privado ou DNS interno do servidor de observabilidade
    endpoint: "${OTEL_GATEWAY_ENDPOINT}:4317"
    tls:
      insecure: true   # dentro da VPC está OK; use TLS em prod com certificado interno

  debug:
    verbosity: basic

service:
  pipelines:
    metrics:
      receivers: [nginx, prometheus]
      processors: [resource/nginx, batch]
      exporters: [otlp, debug]
```

> **Dica**: Defina `OTEL_GATEWAY_ENDPOINT` como variável de ambiente na Task Definition ou via AWS Systems Manager Parameter Store.

### 2.3 Task Definition (JSON simplificado)

```json
{
  "family": "proxy-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::ACCOUNT:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::ACCOUNT:role/ecsTaskRole",
  "containerDefinitions": [
    {
      "name": "proxy",
      "image": "ACCOUNT.dkr.ecr.REGION.amazonaws.com/proxy-openresty:latest",
      "portMappings": [
        { "containerPort": 8081, "protocol": "tcp" }
      ],
      "environment": [
        { "name": "UPSTREAM_HOST", "value": "localhost" },
        { "name": "UPSTREAM_PORT", "value": "5000" }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/proxy",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "proxy"
        }
      },
      "essential": true
    },
    {
      "name": "otel-agent",
      "image": "ACCOUNT.dkr.ecr.REGION.amazonaws.com/proxy-otel-agent:latest",
      "command": ["--config=/etc/otelcol-contrib/config.yaml"],
      "environment": [
        { "name": "OTEL_GATEWAY_ENDPOINT", "value": "10.0.2.50" }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/proxy",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "otel-agent"
        }
      },
      "essential": false,
      "dependsOn": [
        { "containerName": "proxy", "condition": "START" }
      ]
    }
  ]
}
```

> **Sobre o config do agente na Task**: O container OTel espera o arquivo de config em `/etc/otelcol-contrib/config.yaml`. As opções para isso em ECS são:
> 1. **Imagem customizada** — copie o `otel-collector-agent.yaml` na imagem via Dockerfile (mais simples, recomendado)
> 2. **EFS mount** — monte um EFS com o arquivo de config
> 3. **SSM + entrypoint** — baixe o config via `aws ssm get-parameter` no entrypoint

### 2.4 Imagem customizada do agente (opção recomendada)

```dockerfile
# aws/Dockerfile.otel-agent
FROM otel/opentelemetry-collector-contrib:latest

COPY otel-collector-agent.yaml /etc/otelcol-contrib/config.yaml
```

```bash
# Build e push para ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin ACCOUNT.dkr.ecr.us-east-1.amazonaws.com

docker build -f aws/Dockerfile.otel-agent -t proxy-otel-agent:latest .
docker tag proxy-otel-agent:latest ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/proxy-otel-agent:latest
docker push ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/proxy-otel-agent:latest
```

### 2.5 Terraform — Task Definition com Sidecar

Esta é a forma **declarativa e reproduzível** de definir toda a infraestrutura ECS, substituindo o JSON manual da seção 2.3.

#### Estrutura de arquivos sugerida

```
terraform/
  variables.tf          # variáveis reutilizáveis
  ecr.tf                # repositórios ECR das imagens
  iam.tf                # roles de execução e task
  ecs_cluster.tf        # cluster ECS
  ecs_task.tf           # task definition (proxy + sidecar)
  ecs_service.tf        # ECS service + network config
  cloudwatch.tf         # log group
```

---

#### `terraform/variables.tf`

```hcl
variable "aws_region" {
  default = "us-east-1"
}

variable "otel_gateway_endpoint" {
  description = "IP ou DNS privado do servidor de observabilidade"
  default     = "obs.internal"   # use o DNS do Route 53 ou IP direto
}

variable "upstream_host" {
  description = "Host do backend que o proxy encaminha requisições"
  default     = "localhost"
}

variable "upstream_port" {
  default = "5000"
}
```

---

#### `terraform/ecr.tf`

Cria os repositórios para as duas imagens que compõem a Task.

```hcl
resource "aws_ecr_repository" "proxy" {
  name                 = "proxy-openresty"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "otel_agent" {
  name                 = "proxy-otel-agent"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

output "proxy_ecr_url" {
  value = aws_ecr_repository.proxy.repository_url
}

output "otel_agent_ecr_url" {
  value = aws_ecr_repository.otel_agent.repository_url
}
```

---

#### `terraform/iam.tf`

Duas roles são necessárias no ECS:
- **execution role** — usada pela AWS para puxar imagens do ECR e enviar logs ao CloudWatch
- **task role** — usada pelo código dentro dos containers (ex: acessar SSM, S3, etc.)

```hcl
# ── Execution Role ──────────────────────────────────────────────────────────
# Permite que o ECS agent puxe imagens do ECR e envie logs ao CloudWatch

data "aws_iam_policy_document" "ecs_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_execution" {
  name               = "ecsTaskExecutionRole"
  assume_role_policy = data.aws_iam_policy_document.ecs_assume_role.json
}

resource "aws_iam_role_policy_attachment" "ecs_execution_policy" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# ── Task Role ────────────────────────────────────────────────────────────────
# Usada pelo código dentro dos containers (adicione políticas conforme necessário)

resource "aws_iam_role" "ecs_task" {
  name               = "ecsTaskRole"
  assume_role_policy = data.aws_iam_policy_document.ecs_assume_role.json
}
```

---

#### `terraform/cloudwatch.tf`

```hcl
resource "aws_cloudwatch_log_group" "proxy" {
  name              = "/ecs/proxy"
  retention_in_days = 7
}
```

---

#### `terraform/ecs_cluster.tf`

```hcl
resource "aws_ecs_cluster" "main" {
  name = "proxy-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"   # habilita métricas de cluster no CloudWatch
  }
}
```

---

#### `terraform/ecs_task.tf` ← **arquivo principal do sidecar**

Este é o coração da configuração. O argumento `container_definitions` recebe um JSON com **dois containers** — o proxy e o agente como sidecar.

```hcl
resource "aws_ecs_task_definition" "proxy" {
  family                   = "proxy-task"
  network_mode             = "awsvpc"    # obrigatório para Fargate
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"       # 0.5 vCPU total para a task inteira
  memory                   = "1024"      # 1 GB total dividido entre os containers

  execution_role_arn = aws_iam_role.ecs_execution.arn
  task_role_arn      = aws_iam_role.ecs_task.arn

  # jsonencode() monta o JSON inline dentro do HCL — sem arquivo externo
  container_definitions = jsonencode([

    # ── Container 1: proxy (OpenResty) ──────────────────────────────────────
    {
      name      = "proxy"
      image     = "${aws_ecr_repository.proxy.repository_url}:latest"
      essential = true   # se esse container morrer, a task inteira para

      portMappings = [
        {
          containerPort = 8081
          protocol      = "tcp"
        }
      ]

      environment = [
        # O backend Flask roda em outro serviço/container na mesma rede da Task
        { name = "UPSTREAM_HOST", value = var.upstream_host },
        { name = "UPSTREAM_PORT", value = var.upstream_port }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.proxy.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "proxy"
        }
      }
    },

    # ── Container 2: otel-agent (sidecar) ───────────────────────────────────
    {
      name      = "otel-agent"
      image     = "${aws_ecr_repository.otel_agent.repository_url}:latest"
      essential = false   # se o agente falhar, o proxy continua servindo tráfego

      # O config está embutido na imagem (ver Dockerfile.otel-agent na seção 2.4)
      command = ["--config=/etc/otelcol-contrib/config.yaml"]

      environment = [
        # Aponta para o servidor de observabilidade (EC2 + Route 53 ou IP fixo)
        { name = "OTEL_GATEWAY_ENDPOINT", value = var.otel_gateway_endpoint }
      ]

      # O sidecar só inicia após o proxy estar de pé
      # Sem isso, o agente tentaria raspar o nginx_status antes do Nginx existir
      dependsOn = [
        {
          containerName = "proxy"
          condition     = "START"   # aguarda o proxy subir (não necessariamente healthy)
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.proxy.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "otel-agent"   # stream separado do proxy no mesmo log group
        }
      }
    }

  ])
}
```

> **Por que `essential = false` no sidecar?**
> Se o agente cair (ex: gateway de obs offline), o proxy continua servindo tráfego normalmente. A task não é encerrada por causa de um problema na observabilidade.

> **Por que `condition = "START"` e não `"HEALTHY"`?**
> `HEALTHY` exigiria um `healthCheck` configurado no container do proxy. `START` é suficiente para garantir que o Nginx já iniciou antes do agente tentar raspar o `/nginx_status`.

---

#### `terraform/ecs_service.tf`

Cria o ECS Service que mantém a task rodando e conecta ao Load Balancer.

```hcl
resource "aws_ecs_service" "proxy" {
  name            = "proxy-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.proxy.arn
  desired_count   = 1          # número de tasks rodando em paralelo
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.public.id]   # ou subnet privada + NAT Gateway
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true    # necessário se estiver em subnet pública sem NAT
  }

  # Força novo deploy quando a task definition muda
  force_new_deployment = true

  # Ignora mudanças externas na desired_count (ex: auto scaling manual)
  lifecycle {
    ignore_changes = [desired_count]
  }
}
```

---

#### Como aplicar

```bash
# 1. Inicialize o Terraform
terraform init

# 2. Revise o plano
terraform plan -var="otel_gateway_endpoint=obs.internal"

# 3. Aplique
terraform apply -var="otel_gateway_endpoint=obs.internal"

# 4. Após aplicar, faça push das imagens para os ECR criados:
# (use os outputs otel_agent_ecr_url e proxy_ecr_url)
terraform output proxy_ecr_url
terraform output otel_agent_ecr_url
```

#### Dependências entre os recursos Terraform

```
aws_ecr_repository.proxy          ──┐
                                    ├──► aws_ecs_task_definition.proxy
aws_ecr_repository.otel_agent     ──┤
aws_iam_role.ecs_execution        ──┤
aws_iam_role.ecs_task             ──┤
aws_cloudwatch_log_group.proxy    ──┘
                                         │
                                         ▼
                               aws_ecs_service.proxy
                                         │
                                         ▼
                               aws_ecs_cluster.main
```

---

## Parte 3 — Rede e Descoberta de Serviços

### 3.1 Como o agente no ECS encontra o servidor de observabilidade?

Existem três abordagens, da mais simples à mais robusta:

| Abordagem                        | Como funciona                                             | Quando usar                        |
|----------------------------------|-----------------------------------------------------------|------------------------------------|
| **IP Privado fixo**              | EC2 com ENI de IP estático (ex: `10.0.2.50`)              | Ambiente de estudo / MVP           |
| **Route 53 Private Hosted Zone** | DNS interno `obs.internal` aponta para o IP do EC2        | Dev/staging                        |
| **AWS Cloud Map**                | Service Discovery gerenciado pela AWS                      | Prod com múltiplos servidores obs  |

#### Opção mais prática: Route 53 Private Hosted Zone

```hcl
# terraform/dns.tf

resource "aws_route53_zone" "internal" {
  name = "internal"
  vpc {
    vpc_id = aws_vpc.main.id
  }
}

resource "aws_route53_record" "observability" {
  zone_id = aws_route53_zone.internal.zone_id
  name    = "obs.internal"
  type    = "A"
  ttl     = 60
  records = [aws_instance.observability.private_ip]
}
```

No `otel-collector-agent.yaml`, use:
```yaml
exporters:
  otlp:
    endpoint: "obs.internal:4317"
```

### 3.2 Security Groups — Fluxo de tráfego

```
[ECS Task SG] --► porta 4317 --► [Observability SG]
                  (OTLP gRPC)

[VPN / SSM]   --► porta 3000 --► [Observability SG]
                  (Grafana)
```

```hcl
# SG das ECS Tasks — permite saída para o servidor de obs
resource "aws_security_group_rule" "ecs_to_otel" {
  type                     = "egress"
  from_port                = 4317
  to_port                  = 4317
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.observability.id
  security_group_id        = aws_security_group.ecs_tasks.id
}
```

---

## Parte 4 — Armazenamento Persistente (Prometheus & Grafana)

No Docker Compose local, os dados somem ao reiniciar o container. Em produção use EBS:

| Serviço    | Solução AWS                             | Configuração                                  |
|------------|-----------------------------------------|-----------------------------------------------|
| Prometheus | EBS volume montado no EC2               | Volume `/dev/xvdf` montado em `/data/prometheus` |
| Grafana    | EBS volume ou RDS (SQLite → PostgreSQL) | EBS para MVP, RDS para prod com HA            |

```bash
# No EC2, monte o EBS antes de subir o compose:
sudo mkfs -t ext4 /dev/xvdf
sudo mkdir -p /data/prometheus /data/grafana
sudo mount /dev/xvdf /data

# Persiste após reboot:
echo '/dev/xvdf /data ext4 defaults,nofail 0 2' | sudo tee -a /etc/fstab
```

Atualize os volumes no docker-compose do servidor:
```yaml
volumes:
  prometheus-data:
    driver: local
    driver_opts:
      type: none
      device: /data/prometheus
      o: bind
  grafana-data:
    driver: local
    driver_opts:
      type: none
      device: /data/grafana
      o: bind
```

---

## Parte 5 — Imagem do Proxy no ECR

```bash
# 1. Crie o repositório ECR
aws ecr create-repository --repository-name proxy-openresty --region us-east-1

# 2. Faça login no ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin ACCOUNT.dkr.ecr.us-east-1.amazonaws.com

# 3. Build e push da imagem do proxy
docker build -t proxy-openresty ./open-resty
docker tag proxy-openresty:latest ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/proxy-openresty:latest
docker push ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/proxy-openresty:latest
```

---

## Parte 6 — Acesso ao Grafana sem expor porta pública

Como o servidor de observabilidade está em subnet privada, use **SSM Session Manager** para port forwarding:

```bash
# Abra tunnel para o Grafana na sua máquina local
aws ssm start-session \
  --target i-0123456789abcdef0 \
  --document-name AWS-StartPortForwardingSession \
  --parameters '{"portNumber":["3000"],"localPortNumber":["3000"]}'

# Acesse http://localhost:3000 no browser
```

> Isso elimina a necessidade de bastion host, VPN ou IP público no servidor de obs.

---

## Parte 7 — Resumo das Mudanças nos Configs

### Arquivos que **não precisam mudar**

- `otel-collector-gateway.yaml` — idêntico ao local
- `grafana/provisioning/` — idêntico ao local
- `open-resty/nginx.conf.template` — idêntico ao local
- `open-resty/Dockerfile` — apenas precisa ser publicado no ECR

### Arquivos que **mudam**

| Arquivo                     | O que muda                                                              |
|-----------------------------|-------------------------------------------------------------------------|
| `otel-collector-agent.yaml` | `endpoint` do nginx/prometheus: `proxy-open-resty` → `localhost`        |
| `otel-collector-agent.yaml` | `endpoint` do exporter otlp: `otel-collector` → IP/DNS do servidor obs |
| `prometheus.yml`            | target: `otel-collector:8889` → `otel-collector-gateway:8889`           |

### `otel-collector-agent.yaml` — diff das mudanças

```diff
 receivers:
   nginx:
-    endpoint: http://proxy-open-resty:8081/nginx_status
+    endpoint: http://localhost:8081/nginx_status
     collection_interval: 10s

   prometheus:
     config:
       scrape_configs:
         - job_name: 'openresty-custom'
           scrape_interval: 10s
           static_configs:
-            - targets: ['proxy-open-resty:8081']
+            - targets: ['localhost:8081']

 exporters:
   otlp:
-    endpoint: otel-collector:4317
+    endpoint: "${OTEL_GATEWAY_ENDPOINT}:4317"
```

---

## Checklist de Deploy

- [ ] VPC criada com subnets pública e privada
- [ ] Security Groups configurados (ECS Task SG -> Observability SG na porta 4317)
- [ ] EC2 de observabilidade provisionada na subnet privada com IAM role para SSM
- [ ] EBS montado em `/data` no EC2 com dados persistentes
- [ ] `docker-compose.yml` e configs copiados para `/opt/observability/` no EC2
- [ ] `docker compose up -d` executado no EC2
- [ ] ECR repositories criados para `proxy-openresty` e `proxy-otel-agent`
- [ ] Imagens buildadas e publicadas no ECR
- [ ] ECS Cluster criado (Fargate)
- [ ] Task Definition registrada com dois containers (proxy + otel-agent sidecar)
- [ ] ECS Service criado e task rodando
- [ ] Variável `OTEL_GATEWAY_ENDPOINT` definida na Task Definition
- [ ] Route 53 Private Hosted Zone configurada (opcional, mas recomendada)
- [ ] Acesso ao Grafana validado via SSM port forwarding
