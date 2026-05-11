# Plano de Estudos: Escalabilidade & Sistemas Distribuídos

Este módulo aborda as técnicas necessárias para sustentar milhões de usuários e volumes massivos de dados.

## 📚 Objetivos de Estudo

### 1. Particionamento de Dados (Sharding)
- **Horizontal Scaling:** Como dividir uma tabela gigante em múltiplos servidores.
- **Consistent Hashing:** Técnica para redistribuir dados com o mínimo de movimentação possível.
- **Hot Shards:** Como evitar que um único servidor receba todo o tráfego.

### 2. Load Balancers & Proxies
- **Algoritmos:** Round Robin, Least Connections, IP Hashing.
- **Layer 4 vs Layer 7:** Decisões de roteamento baseadas em TCP ou Protocolos de Aplicação.
- **Health Checks:** Monitoramento automático de nós saudáveis.

### 3. Estratégias de Cache
- **Caching Patterns:** Cache-aside, Write-through, Write-back.
- **Políticas de Evicção:** LRU (Least Recently Used) e LFU.
- **Invalidação:** Como manter o cache sincronizado com a fonte da verdade.

### 4. Bancos de Dados Distribuídos
- **Teorema CAP:** Consistência, Disponibilidade e Tolerância a Partição.
- **PACELC:** O trade-off entre latência e consistência na ausência de falhas.
- **Replicação:** Líder-Seguidor vs Multi-Líder vs Leaderless (Quorum).

---

## 🛠️ Projetos Propostos

### [08] Sharding Simulator
Simulador que distribui chaves entre "nós" usando Consistent Hashing e mostra o impacto de adicionar/remover nós.

### [09] Load Balancer Lab
Mini-proxy que distribui requisições entre servidores simulados com diferentes algoritmos.

### [10] High Performance Cache
Implementação de um cache LRU thread-safe com métricas de Hit/Miss.

### [11] CAP Theorem Visualizer
Simulação de um cluster onde você pode causar falhas de rede e observar a divergência de dados.
