# Guia Avançado: Virtual Threads & Alta Vazão (JDK 21+)

Este documento detalha como as Virtual Threads revolucionam o throughput de aplicações I/O-bound e por que o paradigma de "Thread-per-Request" voltou a ser a melhor escolha arquitetural.

---

## 1. O Paradigma: Thread-per-Request

Historicamente, o Java seguia o modelo de **uma thread por requisição**.
*   **Problema:** Threads nativas são caras (1MB de RAM). Para 10.000 requisições, precisaríamos de 10GB de RAM apenas para as threads.
*   **Solução antiga:** Programação Reativa (WebFlux, RxJava). Eficiente, mas extremamente complexa de debugar e escrever.
*   **Nova Solução (Loom):** Virtual Threads. Elas permitem manter o código imperativo (simples) com a eficiência do modelo reativo.

---

## 2. Por que NÃO usar Pool de Virtual Threads?

Este é o erro mais comum de desenvolvedores que estão migrando para o Java 21.

*   **Pools de Threads Nativa:** Servem para limitar o consumo de recursos caros (RAM/CPU).
*   **Virtual Threads:** São baratas e efêmeras. O custo de gerenciar um pool de threads virtuais é maior do que simplesmente criar uma nova e deixá-la morrer.
*   **A Regra de Ouro:** Se você quer limitar o acesso a um recurso (ex: apenas 10 conexões simultâneas ao banco), **use um Semaphore**, não um Thread Pool.

---

## 3. High Throughput e I/O Blocking

As Virtual Threads brilham em operações bloqueantes:
*   Chamadas REST (HttpClient).
*   Consultas JDBC/JPA.
*   Leitura de Arquivos.
*   `Thread.sleep()`.

Quando uma Virtual Thread bloqueia, a JVM realiza o **Unmount** (desmontagem), salvando o estado da pilha na Heap e liberando a **Carrier Thread** para processar a próxima requisição.

---

## 4. Limitações e Cuidados no Mundo Real

### 4.1. Pilhas de Execução Profundas
Embora leves, as Virtual Threads ainda ocupam espaço na Heap. Se suas threads tiverem pilhas de chamadas recursivas muito profundas, o consumo de memória pode subir.

### 4.2. Monitoramento
Ferramentas de profiling clássicas (JVisualVM, JConsole) podem mostrar apenas as Carrier Threads. Para ver as Virtual Threads, você precisará de ferramentas que suportem o novo modelo de dump de threads do Java 21 (`jcmd <pid> Thread.dump_to_file`).

---

## 5. Resumo Arquitetural

| Cenário | Estratégia |
| :--- | :--- |
| **Microserviço Spring Boot** | Use Virtual Threads (`spring.threads.virtual.enabled=true`). |
| **Worker de Processamento de Imagem** | Use um `FixedThreadPool` com Platform Threads. |
| **Web Crawler com 100k sites** | Use Virtual Threads com um `Semaphore` para respeitar os limites dos sites. |

---
*Documentação preparada para a transição definitiva para o modelo de threads leves do Project Loom.*
