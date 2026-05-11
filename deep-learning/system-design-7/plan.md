# Plano de Estudos: System Design & Performance

Este módulo foca na ciência de medir e otimizar a performance de sistemas complexos.

## 📚 Objetivos de Estudo

### 1. Latência vs Throughput (Vazão)
- **Latência:** Tempo que uma única operação leva (ms).
- **Throughput:** Quantas operações o sistema processa por segundo (req/s).
- **Trade-off:** Como otimizar um sem degradar o outro excessivamente.

### 2. Métricas de Performance
- **P50, P95, P99:** Por que a média mente e como as métricas de cauda mostram a realidade do usuário.
- **Throughput Saturation:** O ponto onde adicionar carga aumenta a latência exponencialmente.

### 3. Leis Fundamentais
- **Little's Law (`L = λW`):** Calcular a capacidade necessária baseada na taxa de chegada.
- **Amdahl's Law:** Entender que a parte sequencial do código limita o ganho do paralelismo.

### 4. Profiling e Diagnóstico
- Identificação de **Bottlenecks** (Gargalos).
- Diferença entre sistemas **CPU-Bound** (Cálculos) e **I/O-Bound** (Rede/Disco).

---

## 🛠️ Projetos Propostos

### [07] Latency Analyzer Tool
Uma ferramenta Java que simula um pipeline de processamento e gera um relatório de latência (P50/P99) e vazão total sob diferentes níveis de concorrência.
