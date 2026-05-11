# Guia Avançado: Locks & Synchronizers (JDK 21+)

Este documento detalha como controlar o acesso a recursos compartilhados e coordenar múltiplas threads através das ferramentas do pacote `java.util.concurrent`.

---

## 1. O Problema da Concorrência: Race Conditions

Quando múltiplas threads tentam ler e escrever no mesmo dado ao mesmo tempo, ocorre uma **Race Condition** (Condição de Corrida). 

*   **Solução clássica:** `synchronized` (Simples, mas menos flexível).
*   **Solução moderna:** `ReentrantLock` (Flexível, suporta timeouts e interrupções).

---

## 2. ReentrantLock: O Lock Inteligente

O `ReentrantLock` é uma implementação de exclusão mútua (`Mutex`).

### 2.1. Por que usar ReentrantLock em vez de synchronized?
1.  **`tryLock()`:** Tenta adquirir o lock por um tempo determinado. Se não conseguir, a thread pode fazer outra coisa (evita deadlock infinito).
2.  **`lockInterruptibly()`:** Permite que uma thread que está esperando um lock seja interrompida (coisa que o `synchronized` não permite).
3.  **Justiça (Fairness):** Você pode configurar o lock para dar prioridade para a thread que está esperando há mais tempo (`new ReentrantLock(true)`).

**Cuidado de Expert:** Sempre use o lock dentro de um bloco `try-finally` para garantir que o `unlock()` seja chamado, mesmo em caso de erro.

---

## 3. Semaphores: Controle de Vazão

O `Semaphore` (Semáforo) não é um lock de exclusão mútua, mas sim um **Contador de Permissões**.

*   **Utilidade:** Limitar o acesso a um recurso finito (Ex: conexões de banco de dados, vagas em um estacionamento, número de downloads simultâneos).
*   **Operações:**
    *   `acquire()`: Pede uma permissão (bloqueia se não houver).
    *   `release()`: Devolve a permissão.

---

## 4. Outros Sincronizadores Essenciais

### 4.1. CountDownLatch (Contagem Regressiva)
Uma barreira que faz uma thread esperar até que outras tarefas terminem (Ex: um servidor que só inicia após carregar 3 configurações em paralelo).

### 4.2. CyclicBarrier (Ponto de Encontro)
Permite que um conjunto de threads espere umas pelas outras em um ponto comum. Quando a última chega, todas são liberadas (Ex: processamento em fases).

### 4.3. ReadWriteLock
Permite que **múltiplas threads leiam** ao mesmo tempo, mas apenas **uma thread escreva**. 
*   **Regra:** Se alguém está lendo, ninguém escreve. Se alguém quer escrever, ninguém lê.
*   **Ganho:** Performance massiva em caches que são muito lidos e pouco alterados.

---

## 5. Resumo para Uso Prático

| Ferramenta | Quando usar? | Analogia |
| :--- | :--- | :--- |
| **ReentrantLock** | Apenas uma thread pode mexer no dado por vez. | Chave única de um banheiro. |
| **Semaphore** | Limitar o número de usuários simultâneos. | Senhas em uma fila de banco. |
| **CountDownLatch** | Esperar um grupo de tarefas antes de seguir. | Portão de largada de uma corrida. |
| **ReadWriteLock** | Muitas leituras e poucas escritas. | Mural de avisos (todos leem, um só edita). |

---
*Documentação preparada para aprofundamento em controle de estado compartilhado e sincronização de threads.*
