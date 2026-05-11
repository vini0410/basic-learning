# Guia Avançado: CompletableFuture & Programação Assíncrona (JDK 21+)

Este documento detalha como o Java implementa o modelo de programação reativa e assíncrona através do `CompletableFuture`, permitindo a orquestração de tarefas sem bloquear as threads do sistema.

---

## 1. O Salto: de Future para CompletableFuture

O `Future` clássico (Java 5) era limitado: você podia apenas perguntar "já terminou?" ou travar a execução com `.get()`. O `CompletableFuture` (Java 8+) introduziu o modelo **Push**, onde você define o que deve acontecer **quando** a tarefa terminar.

### 1.1. Principais Diferenças
| Característica | Future | CompletableFuture |
| :--- | :--- | :--- |
| **Encaixe de Tarefas** | Impossível sem bloquear. | Permite `thenApply`, `thenCompose`, etc. |
| **Combinação** | Manual e complexa. | Nativa via `thenCombine` ou `allOf`. |
| **Tratamento de Erro** | Requer try/catch no `.get()`. | Reativo via `exceptionally` ou `handle`. |
| **Conclusão Manual** | Não permite. | Permite forçar um resultado via `.complete()`. |

---

## 2. Orquestração de Fluxos (Pipeline)

O poder do `CompletableFuture` está em criar pipelines de execução:

### 2.1. Criação e Transformação
*   **`supplyAsync(Supplier)`:** Inicia uma tarefa assíncrona que retorna um valor.
*   **`runAsync(Runnable)`:** Inicia uma tarefa assíncrona que não retorna nada.
*   **`thenApply(Function)`:** Transforma o resultado (semelhante ao `map` do Stream).
*   **`thenAccept(Consumer)`:** Consome o resultado final (geralmente para logs ou salvar no DB).

### 2.2. Combinação de Múltiplas Tarefas
*   **`thenCombine`:** Espera duas tarefas independentes terminarem e combina seus resultados.
*   **`thenCompose`:** Útil quando a segunda tarefa depende do resultado da primeira (FlatMap).
*   **`allOf(f1, f2, ...)`:** Espera que **todas** as tarefas terminem.
*   **`anyOf(f1, f2, ...)`:** Retorna o resultado da **primeira** tarefa que completar (Leilão de APIs).

---

## 3. Gerenciamento de Exceções Reativo

Em um fluxo assíncrono, as exceções ocorrem em threads diferentes da thread principal.

*   **`exceptionally(ex -> ...)`:** Um "catch" reativo que permite retornar um valor padrão em caso de erro.
*   **`handle((res, ex) -> ...)`:** Permite processar tanto o resultado quanto a exceção, transformando-os se necessário.
*   **`whenComplete((res, ex) -> ...)`:** Ação final (como um "finally") que não altera o resultado do fluxo.

---

## 4. Onde as Virtual Threads se encaixam?

Com a chegada do Java 21, surgiu a dúvida: *Ainda preciso de CompletableFuture se tenho Virtual Threads?*

**A resposta é SIM.**
*   **Virtual Threads** resolvem o problema do **bloqueio** (podemos ter milhões de threads esperando I/O).
*   **CompletableFuture** resolve o problema da **orquestração** (como coordenar 10 chamadas de API paralelas e combinar os dados).

**Dica de Expert:** Use Virtual Threads como o *Executor* para seus CompletableFutures para obter o melhor dos dois mundos (Orquestração elegante + Threads leves).
```java
ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();
CompletableFuture.supplyAsync(() -> fetch(), vExecutor);
```

---

## 5. Boas Práticas e Anti-patterns

1.  **Não use `.join()` ou `.get()` no meio do fluxo:** Isso anula o benefício da programação assíncrona. Use esses métodos apenas no ponto mais externo da aplicação (ex: no Controller ou no final do Main).
2.  **Sempre forneça um Executor:** Por padrão, o `CompletableFuture` usa o `ForkJoinPool.commonPool()`. Para tarefas de I/O pesado, passe seu próprio pool (preferencialmente de Virtual Threads no Java 21).
3.  **Timeout é obrigatório:** APIs falham. Use `orTimeout(delay, unit)` ou `completeOnTimeout(value, delay, unit)` para garantir que seu sistema não fique esperando para sempre.
4.  **Cuidado com Side-Effects:** Tente manter as funções dentro do `thenApply` puras (sem alterar variáveis externas) para evitar problemas de concorrência.

---
*Documentação preparada para design de sistemas distribuídos e reativos de alta performance.*
