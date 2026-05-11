# Guia Avançado: Concorrência e Alta Performance em Java (JDK 21+)

Este documento detalha a evolução do modelo de threading do Java, desde as Platform Threads (nativas) até a revolução do Project Loom (Virtual Threads).

---

## 1. O Modelo Clássico: Platform Threads (Threads de Plataforma)

Até o Java 20, toda `java.lang.Thread` era uma **Platform Thread**. Isso significa que havia um mapeamento de **1:1** entre a Thread da JVM e a Thread do Sistema Operacional (Kernel Thread).

### 1.1. Anatomia e Custos
*   **Memória (Stack):** Cada thread reserva, por padrão, **1 MB** de Stack (pilha de execução). Criar 1.000 threads consome imediatamente ~1 GB de RAM.
*   **Criação:** Envolve uma "syscall" (chamada de sistema) ao Kernel do SO. É uma operação cara em termos de ciclos de CPU.
*   **Escalabilidade:** O limite de threads é ditado pela memória RAM e pela capacidade do Scheduler do SO. Em servidores comuns, passar de 5.000 a 10.000 threads costuma degradar severamente a performance.

### 1.2. O Problema do Context Switching (Troca de Contexto)
Quando o SO tem mais threads do que núcleos de CPU, ele precisa alternar entre elas.
1.  **Salvar o Estado:** Registradores, ponteiro de instrução e cache da Thread A são salvos.
2.  **Carregar o Estado:** Os dados da Thread B são carregados nos registradores.
*   **Consequência:** Em aplicações I/O-bound (que esperam banco de dados/API), a CPU gasta mais tempo trocando de contexto do que executando lógica de negócio.

---

## 2. A Evolução: Virtual Threads (Project Loom)

Introduzidas como feature final no Java 21, as **Virtual Threads** mudam o mapeamento de 1:1 para **M:N** (Muitas threads virtuais para poucas threads de plataforma).

### 2.1. O Conceito de M:N
As Virtual Threads são gerenciadas pela **JVM**, não pelo Sistema Operacional. 
*   Elas são objetos leves armazenados na **Heap** (memória comum de objetos).
*   Uma Virtual Thread custa apenas alguns **Bytes ou KiloBytes** de memória, permitindo criar **milhões** delas no mesmo hardware.

### 2.2. Carrier Threads (Threads Transportadoras)
As Virtual Threads não rodam no vácuo. Elas precisam de uma **Carrier Thread** (uma Platform Thread real) para chegar à CPU.
*   **Mounting (Montagem):** A JVM atribui uma Virtual Thread a uma Carrier Thread para execução.
*   **Unmounting (Desmontagem):** Se a Virtual Thread encontrar uma operação bloqueante (ex: `Thread.sleep`, `socket.read`, `Lock.lock`), a JVM a remove da Carrier Thread, salva seu estado na Heap e libera a Carrier Thread para outra tarefa imediatamente.

### 2.3. Carrier Threads vs. Virtual Threads (Analogia do Expert)
*   **Virtual Threads = Passageiros (Muitos):** Querem chegar ao destino (completar a tarefa).
*   **Carrier Threads = Ônibus/Assentos (Poucos):** São o recurso real que se move.
*   **O "Pulo do Gato":** No modelo antigo, se um passageiro dormisse, ele continuava ocupando o assento. No Loom, se o passageiro dorme, ele é retirado do assento para que outro possa sentar, e só volta quando acordar.

---

## 3. CPU-Bound vs. I/O-Bound: Onde aplicar cada uma?

| Tipo de Trabalho | Recomendação | Por que? |
| :--- | :--- | :--- |
| **CPU-Bound** (Cálculos, Filtros de Imagem) | **Platform Threads** (em número igual aos Cores) | A CPU estará sempre ocupada. Trocar threads virtuais só adicionaria overhead de gerenciamento da JVM sem ganho real. |
| **I/O-Bound** (APIs, Banco de Dados, Microserviços) | **Virtual Threads** | Quase todo o tempo é espera. Virtual Threads permitem que o servidor lide com milhares de conexões simultâneas com quase zero de desperdício de CPU. |

---

## 4. Perigos e Boas Práticas (Nível Pleno/Sênior)

### 4.1. Thread Pinning (Fixação)
A Virtual Thread fica "presa" à Carrier Thread e não pode ser desmontada se:
1.  Estiver dentro de um bloco `synchronized`.
2.  Estiver executando código nativo (JNI).
*   **Solução:** Substitua `synchronized` por `java.util.concurrent.locks.ReentrantLock`.

### 4.2. Não faça Pool de Virtual Threads
Virtual Threads são baratas e efêmeras. 
*   **Errado:** Usar um Pool de threads para limitar Virtual Threads.
*   **Certo:** Crie uma nova Virtual Thread para cada tarefa (`Executors.newVirtualThreadPerTaskExecutor()`). Se precisar limitar o acesso a um recurso (como um banco de dados), use um **Semaphore**.

### 4.3. ThreadLocal
Tenha cautela com `ThreadLocal` em Virtual Threads. Como você pode ter milhões de threads, se cada uma guardar um objeto pesado no `ThreadLocal`, você terá um problema massivo de memória na Heap.

---

## 5. Resumo para Decisão Arquitetural

1.  **Precisa de throughput massivo em I/O?** Vá de Virtual Threads.
2.  **Precisa de processamento matemático pesado?** Use Platform Threads fixas ao número de núcleos.
3.  **Sincronização?** Fuja do `synchronized` clássico em ambientes de Virtual Threads para evitar o Pinning.

---
*Documentação preparada para fins de aprofundamento técnico em Engenharia de Software e Alta Performance.*
