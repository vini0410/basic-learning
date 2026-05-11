# Roadmap de Projetos Práticos: Spring Boot vs. Quarkus

Este documento detalha dois cenários de aplicações reais para aplicar conceitos avançados de Multithreading, Escalabilidade e System Design, movendo além das simulações básicas.

---

## Projeto 1: Motor de Clearing e Liquidação Financeira (Spring Boot 3.x)
Foco em **Consistência (ACID)**, **Transacionalidade** e **Segurança**.

### O Cenário
Um sistema "Core Banking" que processa liquidações financeiras em massa. Deve garantir que cada transação seja validada, descontada de uma conta e creditada em outra de forma atômica, mesmo sob alta concorrência de acessos simultâneos à mesma conta.

### Arquitetura e Conceitos Aplicados
- **Java 21 Virtual Threads:** Utilização do modelo "thread-per-request" escalável para operações de I/O de banco de dados e chamadas externas (KYC/Fraude).
- **Distributed Locking (Redis/Redlock):** Implementação de travas distribuídas para evitar o problema de "double spending" quando múltiplos nós do cluster tentam debitar a mesma conta simultaneamente.
- **Padrão Transactional Outbox:** Uso de eventos (Kafka/RabbitMQ) para garantir que a notificação de sucesso da transação ocorra apenas se o commit no banco de dados for bem-sucedido.
- **Database Sharding:** Simulação de particionamento de dados por `tenant_id` ou `account_range` para suportar crescimento horizontal do banco de dados.
- **Resilience4j:** Implementação de Circuit Breaker e Retry para chamadas a serviços de terceiros (Gateways de pagamento).

### Comportamento e Cenários Críticos
- **Double Spending Protection:** O sistema deve adquirir um lock distribuído no Redis por `account_id` com TTL de 5s antes de qualquer alteração de saldo. Se o lock falhar após 3 tentativas, a transação deve ser abortada.
- **Garantia de Entrega (Outbox):** Em caso de falha no Broker (Kafka), a transação bancária NÃO deve ser revertida se já commitada no DB. Um worker agendado deve reprocessar a tabela `outbox` até que a mensagem seja entregue (at-least-once).
- **Consistência Eventual de Saldo:** O saldo em cache (Redis) deve ser invalidado imediatamente após o commit no Postgres para garantir que a próxima leitura reflita o estado real.

### Mapeamento de Erros e Respostas
- `409 Conflict`: Quando uma transação duplicada (mesmo `idempotency-key`) é detectada ou lock concorrente expira.
- `422 Unprocessable Entity`: Saldo insuficiente para realizar o débito.
- `503 Service Unavailable`: Falha crítica em dependências externas (KYC/Fraude) após exaustão de retentativas.

### Plano de Testes e Validação
- **Testes de Integração:** Uso de **Testcontainers** para subir instâncias reais de PostgreSQL, Redis e Kafka durante o build.
- **Testes de Concorrência:** Script em **k6** simulando 50 threads simultâneas tentando debitar a mesma conta com saldo limitado (deve garantir saldo zero, nunca negativo).
- **Testes de Resiliência:** Simular queda do Kafka via Docker para validar se o `Transactional Outbox` persiste as mensagens corretamente no banco.

---

## Projeto 2: Engine de Preços Dinâmicos e Disponibilidade (Quarkus)
Foco em **Baixa Latência**, **Throughput Massivo** e **Resiliência Cloud Native**.

### O Cenário
Uma plataforma inspirada em Uber/Booking que calcula preços de serviços em tempo real com base em variáveis voláteis (demanda local, clima, tráfego e estoque de parceiros). O sistema deve responder em milissegundos e priorizar a disponibilidade sobre a consistência total.

### Arquitetura e Conceitos Aplicados
- **Programação Reativa (Mutiny):** Orquestração não-bloqueante de múltiplas chamadas paralelas a APIs de terceiros e agregadores de dados.
- **Teorema CAP (Modelo AP):** Configuração do sistema para favorecer a Disponibilidade e Tolerância a Partição. Se um nó de dados falhar, o sistema retorna um preço "estimado" em vez de um erro.
- **High Performance L1/L2 Cache:** 
    - **L1 (Caffeine):** Cache em memória local para dados ultra-frequentes.
    - **L2 (Redis):** Cache distribuído para resultados de cálculos complexos.
- **GraalVM Native Image:** Compilação nativa para garantir tempos de boot sub-second em ambientes de auto-scaling (Kubernetes HPA).
- **Load Balancing Baseado em Latência:** Simulação de roteamento inteligente onde o tráfego é direcionado para as instâncias com menor tempo de resposta atual.

### Comportamento e Cenários Críticos
- **Surge Pricing Logic:** O sistema deve detectar aumentos de demanda em micro-regiões (Geofencing) e aplicar multiplicadores de preço em tempo real. Se o cálculo do multiplicador demorar > 50ms, o sistema deve usar o último multiplicador conhecido (fallback).
- **Invalidação de Cache L2 e Stale-While-Revalidate:** O cache L2 (Redis) terá TTL de 60s, mas após 30s o sistema deve tentar atualizar o dado em background se houver requisições (evitando o "cache stampede").
- **Auto-Scaling Rápido:** A aplicação deve estar pronta para receber tráfego em menos de 100ms (Cold Start) usando imagens nativas GraalVM. O Quarkus deve ser configurado com `quarkus.native.builder-image` otimizado para containers.
- **Resiliência de Cache:** Se o Redis (L2) estiver indisponível, a aplicação deve degradar graciosamente consultando diretamente o DB NoSQL (ScyllaDB) ou mantendo apenas o L1, aceitando um aumento controlado na latência (p99).

### Mapeamento de Erros e Respostas
- `206 Partial Content`: Retornado quando o preço é calculado com sucesso, mas o multiplicador de demanda falhou e foi usado um fallback "estático". O corpo da resposta deve conter o campo `warnings` listando as fontes não consultadas.
- `400 Bad Request`: Parâmetros de latitude/longitude inválidos, fora do formato decimal ou fora da área de cobertura do serviço.
- `429 Too Many Requests`: Acionamento de Rate Limiting por API Key para proteção da infraestrutura sob ataque.
- `504 Gateway Timeout`: Quando a agregação reativa de múltiplas fontes (Mutiny) excede o tempo limite global de 500ms para o cálculo do preço.

### Plano de Testes e Validação
- **Benchmark de Performance:** Comparação de latência (p99) entre a versão rodando em JVM tradicional vs. Native Image (GraalVM).
- **Chaos Engineering com WireMock:** Injetar latência de rede variável (20ms a 2000ms) nas APIs externas de clima/tráfego para validar os comportamentos de fallback e circuit breaker do Mutiny.
- **Teste de Carga Geofenced:** Uso de script **k6** enviando requisições com coordenadas geográficas randômicas em um polígono específico para validar a lógica de `Surge Pricing`.
- **Validação de Startup:** Medir o `time to first request` após o boot do container nativo para garantir que está dentro da meta de < 100ms.
- **Dependências de Teste:**
    - Docker: `scylladb/scylla:latest`, `wiremock/wiremock:latest-alpine`.
    - Ferramentas: **Hey** (para testes rápidos de throughput), Postman (Collection com exemplos de payloads 206 e 400).

---

## Infraestrutura de Dependências (Docker Stack)

| Serviço | Imagem Docker | Finalidade |
| :--- | :--- | :--- |
| **PostgreSQL 16** | `postgres:16-alpine` | Persistência Relacional (Projeto 1) |
| **Redis 7** | `redis:7-alpine` | Distributed Lock e Caching (Ambos) |
| **Kafka** | `confluentinc/cp-kafka:7.5.0` | Mensageria e Event Sourcing (Projeto 1) |
| **ScyllaDB / Mongo** | `scylladb/scylla:latest` | Persistência NoSQL de alta escrita (Projeto 2) |
| **Prometheus** | `prom/prometheus:latest` | Coleta de métricas de performance |

---

## Comparativo Técnico para Implementação

| Recurso | Projeto Financeiro (Spring) | Projeto Real-Time (Quarkus) |
| :--- | :--- | :--- |
| **Prioridade de Negócio** | Integridade e Auditoria | Experiência do Usuário (Velocidade) |
| **Modelo de Concorrência** | Virtual Threads (Imperativo) | Reativo (Event-Loop) + Virtual Threads |
| **Persistência Principal** | PostgreSQL (Relacional/JPA) | ScyllaDB ou MongoDB (NoSQL) |
| **Comunicação Inter-Srv** | Kafka (Event-Driven) | gRPC ou REST Reativo |
| **Infraestrutura** | Tradicional (JVM Otimizada) | Serverless / Native Containers |
