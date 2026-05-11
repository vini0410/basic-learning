# Plano de Estudos: Concorrência e Alta Performance em Java

Este plano foca nos pilares de multithreading, processamento assíncrono e as novas capacidades do Project Loom (Virtual Threads).

---

## 1. Tópicos de Estudo

### 🧵 A. Threading em Java (Fundamentos)
- **O que estudar:** Ciclo de vida de uma Thread, `Runnable` vs `Callable`, `ThreadLocal`, Interrupções e Volatile.
- **Projeto:** **Simple Image Filter** -> Um processador de pixels que divide uma imagem em quadrantes, onde cada Thread processa uma parte de forma independente.

### ⚙️ B. Executors & ForkJoin Framework
- **O que estudar:** `ThreadPoolExecutor`, tipos de pools (Fixed, Cached, Scheduled), Work-Stealing e `RecursiveTask`.
- **Projeto:** **Big Data File Processor** -> Um sistema que lê um arquivo de log massivo (GBs) e usa ForkJoin para contar ocorrências de erros via divisão e conquista.

### ⚡ C. CompletableFuture & Programação Reativa
- **O que estudar:** Composição de stages (`thenApply`, `thenCombine`), tratamento de exceções assíncronas e conceitos básicos de Backpressure (Flow API).
- **Projeto:** **Travel Aggregator** -> Um simulador que consulta preços em várias "APIs" (com delays variados) e retorna o melhor preço assim que o primeiro grupo de respostas chegar.

### 🔒 D. Gerenciamento de Concorrência (Locks & Synchronizers)
- **O que estudar:** `ReentrantLock`, `ReadWriteLock`, `Semaphore`, `CountDownLatch` e `CyclicBarrier`.
- **Projeto:** **Multi-Level Parking System** -> Um sistema onde vagas de estacionamento são controladas por Semáforos e a entrada/saída de veículos por Locks, garantindo consistência em alta concorrência.

### 🚀 E. Virtual Threads (Java 21+)
- **O que estudar:** Carrier threads, montagem/desmontagem, limitações (pinning) e por que não usar pools para Virtual Threads.
- **Projeto:** **High-Throughput Echo Server** -> Um servidor que lida com 10.000 conexões simultâneas simuladas, mantendo uma thread por requisição sem esgotar a memória da JVM.

### 📊 F. System Design Fundamentals (Performance)
- **O que estudar:** Latência vs Throughput, Little's Law, Amdahl's Law, identificação de gargalos (bottlenecks) e ferramentas de profiling.
- **Projeto:** **Latency Analyzer Tool** -> Uma ferramenta que simula diferentes cargas de trabalho e gera métricas de P95/P99, comparando como diferentes tamanhos de buffers e threads afetam a vazão total.

### 🏗️ G. Projetando Sistemas Escaláveis
- **O que estudar:** 
    - **Particionamento:** Vertical vs Horizontal (Sharding), Consistent Hashing.
    - **Load Balancers:** Algoritmos (Round Robin, Least Connections), Layer 4 vs Layer 7.
    - **Caching:** Write-through, Write-back, Cache-aside e Eviction policies (LRU, LFU).
    - **Bancos de Dados Distribuidos:** Teorema CAP, Replicação (Líder/Seguidor), Replicação Multi-líder.
- **Projeto:** **E-commerce Scaling Simulator** -> Um simulador modular onde você pode "ligar/desligar" um Load Balancer, adicionar "shards" de banco de dados e observar o impacto na disponibilidade e performance do sistema sob stress.

---

## 2. Projetos Híbridos (Integração)

Estes projetos visam unir os conceitos para entender como eles coexistem em aplicações reais.

### 🏗️ Projeto Híbrido 1: Web Crawler Inteligente
- **Assuntos:** `Virtual Threads` + `CompletableFuture` + `Locks`.
- **Descrição:** Um crawler que usa Virtual Threads para realizar o I/O de rede (milhares de sites), utiliza `CompletableFuture` para processar o conteúdo das páginas de forma assíncrona e um `ReadWriteLock` para gerenciar o acesso a um índice de palavras buscadas em memória.

### 📈 Projeto Híbrido 2: Sistema de Trading em Tempo Real
- **Assuntos:** `Executors` + `ForkJoin` + `Synchronizers`.
- **Descrição:** Recebe um stream de cotações. Usa um `ExecutorService` para gerenciar a entrada de dados, o `ForkJoin Framework` para calcular indicadores técnicos complexos (como médias móveis de 200 dias) em paralelo, e um `CyclicBarrier` para sincronizar o envio de ordens de compra/venda somente quando todos os indicadores forem calculados.

### 🏦 Projeto Híbrido 3: Simulador de Transações Bancárias Distribuídas
- **Assuntos:** `Semaphore` + `CompletableFuture` + `Virtual Threads`.
- **Descrição:** Simula milhares de transferências entre contas. Usa `Virtual Threads` para cada transação, um `Semaphore` para limitar o número de conexões simultâneas a um banco de dados simulado e `CompletableFuture` para gerar comprovantes e enviar notificações de sucesso de forma não-bloqueante.

---

## 🛠️ Sugestão de Execução
1. Comece pelos fundamentos (A e D) para entender o custo do bloqueio.
2. Evolua para abstrações de pool (B).
3. Entre no mundo assíncrono moderno (C e E).
