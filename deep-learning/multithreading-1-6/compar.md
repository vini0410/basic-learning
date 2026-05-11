# Comparativo de Modelos de Multithreading em Java

Esta tabela compara as diferentes abordagens de concorrência em Java, desde o uso de threads de plataforma até as modernas Virtual Threads do Project Loom.

| Modelo | Foco Principal | Pontos Fortes | Pontos Fracos | Performance | Custo Operacional | Complexidade |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Platform Threads (Standard)** | Controle total e tarefas de longa duração. | Controle granular (prioridade, interrupção); mapeamento 1:1 com threads do SO. | Caro para criar e trocar contexto; Consome muita memória (Stack ~1MB). | Baixa escalabilidade (Limite de ~1-2k threads por JVM). | **Alto**: Gestão manual de vida e estado da thread. | **Alta**: Exige gestão cuidadosa de deadlocks e race conditions. |
| **ExecutorService (Pools)** | Reuso de threads e gestão de recursos. | Abstração eficiente do ciclo de vida; Evita o overhead de criação constante. | Risco de saturação da fila; Dificuldade em tarefas bloqueantes de I/O massivo. | Boa para tarefas de CPU; Limitada pelo tamanho do pool para tarefas bloqueantes. | **Médio**: Menor que threads avulsas, mas ainda exige ajuste fino de pool. | **Média**: Abstrai a criação, mas o tratamento de resultados pode ser complexo. |
| **ForkJoin Framework** | Divisão e conquista (tasks paralelas). | Algoritmo de Work-Stealing; Excelente para tarefas recursivas que dividem dados. | Ineficiente para tarefas que bloqueiam (I/O); Curva de aprendizado íngreme. | Alta para computação intensiva e recursiva (CPU-bound). | **Médio**: Gerenciado pela JVM, mas exige lógica de quebra de tarefas. | **Alta**: Requer entendimento profundo de recursão e segmentação de dados. |
| **CompletableFuture (Async)** | Encadeamento e programação reativa. | Estilo não-bloqueante; Composição elegante de pipelines; Tratamento de erros fluído. | Código pode virar "Callback Hell" se mal estruturado; Difícil de debugar (Stack traces). | Alta para I/O não-bloqueante; Minimiza o número de threads paradas. | **Baixo**: Uso eficiente de threads de pool (comumente ForkJoinPool). | **Média/Alta**: Exige mudança de paradigma (estilo imperativo para funcional). |
| **Virtual Threads (Java 21+)** | Alta concorrência e escalabilidade massiva. | Milhões de threads por JVM; Mantém o código imperativo simples; Baixo custo de criação. | Problemas de "pinning" com locks sincronizados; Ineficiente para tarefas puras de CPU. | Máxima para I/O bloqueante (Servlet, DB, API); Similar a Platform para CPU. | **Muito Baixo**: Montagem/desmontagem rápida em "Carrier Threads" (gestão JVM). | **Baixa/Média**: Permite escrever código sequencial que escala massivamente. |

---

## 💡 Resumo de Quando Usar Cada Um

1.  **Platform Threads:** Use quando precisar de controle absoluto sobre a thread ou para tarefas que rodam durante todo o ciclo de vida da aplicação (Ex: Monitoramento de baixa latência).
2.  **ExecutorService:** O padrão para a maioria das aplicações corporativas legadas ou que lidam com tarefas limitadas de CPU.
3.  **ForkJoin:** Ideal para processamento de Big Data em memória, algoritmos recursivos e ordenação paralela.
4.  **CompletableFuture:** Melhor escolha para orquestração de múltiplas APIs assíncronas ou quando se quer evitar o bloqueio de threads de I/O.
5.  **Virtual Threads:** A escolha moderna para servidores web e aplicações que lidam com milhares de requisições simultâneas que dependem de I/O (Banco de Dados, HTTP).
