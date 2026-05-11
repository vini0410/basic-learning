# Master Plan: Deep Learning - Arquitetura e Alta Performance

Este repositório é um guia de estudos prático focado em construir sistemas escaláveis, performáticos e resilientes. O aprendizado está dividido em três grandes pilares:

## 🧵 1. Multithreading & Concorrência
**Onde:** `/multithreading`
**Foco:** Domínio da JVM e aproveitamento máximo de hardware.
- Fundamentos de Threads, Pools de Execução e ForkJoin.
- Programação Assíncrona com CompletableFuture.
- Locks modernos e Sincronizadores.
- **Project Loom:** Escalabilidade massiva com Virtual Threads.

## 📊 2. System Design & Performance
**Onde:** `/system-design`
**Foco:** Medição, análise e otimização de sistemas.
- Entendimento profundo de Latência vs Throughput.
- Leis de Little e Amdahl para prever capacidade.
- Identificação de gargalos (CPU vs I/O bound).
- Ferramentas de profiling e métricas de cauda (P99).

## 🏗️ 3. Scalability & Distributed Systems
**Onde:** `/scalability`
**Foco:** Estratégias para sistemas que crescem horizontalmente.
- **Data Partitioning:** Sharding e Consistent Hashing.
- **Load Balancing:** Distribuição de carga e Failover.
- **Caching:** Estratégias de escrita e invalidação (LRU, Write-through).
- **Distributed Databases:** Teorema CAP e modelos de consistência.

---

## 🛠️ Como Estudar
1. Leia o `plan.md` dentro de cada pasta para entender o roteiro de estudos daquele assunto.
2. Explore os projetos numerados dentro de cada pasta (01, 02, etc) para ver a implementação prática.
3. Tente rodar os simuladores para observar o comportamento dos sistemas sob diferentes cargas.
