# Clearing & Settlement Engine - Documentação Técnica Detalhada

Este documento fornece uma visão profunda da arquitetura, decisões de design e lições de engenharia aprendidas no desenvolvimento deste motor de liquidação financeira.

---

## 1. Visão Geral do Sistema
O objetivo central deste sistema é processar transferências de valores entre contas bancárias com **garantia de atomicidade**, mesmo sob carga extrema. O sistema não apenas atualiza saldos, mas garante que cada movimento seja auditado e que sistemas externos sejam notificados via mensageria de forma confiável.

---

## 2. Arquitetura de Concorrência: Virtual Threads (Java 21)

### O Porquê das Virtual Threads
Tradicionalmente, servidores Java (como o Tomcat padrão) usam o modelo "Thread-per-Request", onde cada requisição ocupa uma thread do Sistema Operacional (Platform Thread). Isso limita a escalabilidade, pois threads do SO são caras em termos de memória e troca de contexto.

Neste projeto, habilitamos as **Virtual Threads**:
- **Escalabilidade Massiva**: Elas são leves e gerenciadas pela JVM, permitindo que o servidor aceite milhares de conexões simultâneas.
- **Eficiência de I/O**: Quando uma Virtual Thread faz uma chamada ao Banco de Dados ou Redis, ela é "suspensa" e a thread real do SO é liberada para processar outra tarefa, retornando apenas quando o I/O termina.

---

## 3. Sincronização Distribuída: Redisson (Redis)

### Estratégia de Lock
Para evitar que duas transferências simultâneas envolvendo a mesma conta causem corrupção de saldo (Race Conditions), utilizamos Locks Distribuídos via Redis.

**Algoritmo de Prevenção de Deadlock:**
Ao transferir da Conta A para a Conta B, o sistema precisa de dois locks. Se uma thread travasse A e esperasse por B, enquanto outra travasse B e esperasse por A, teríamos um Deadlock.
- **Solução**: Implementamos a ordenação lexicográfica.
  ```java
  String firstLock = accountA.compareTo(accountB) < 0 ? accountA : accountB;
  String secondLock = firstLock.equals(accountA) ? accountB : accountA;
  ```
- Isso garante que qualquer thread, independente da direção da transferência, sempre tentará adquirir os locks na mesma ordem, eliminando a possibilidade de espera circular.

---

## 4. Confiabilidade: Outbox Pattern & Kafka

### O Desafio da Consistência Dual
Escrever no Banco de Dados e enviar para o Kafka são duas operações distintas. Se o banco confirmar mas o Kafka falhar, o sistema externo nunca saberá da liquidação.

### A Implementação do Outbox
1. **Transação Atômica**: O débito, o crédito, a transação e o evento de "Outbox" são salvos no PostgreSQL dentro de uma única transação atômica.
2. **Relay Assíncrono**: O componente `OutboxRelay` (agendado via `@Scheduled`) busca eventos não processados e os envia ao Kafka.
3. **Garantia de Entrega**: Somente após a confirmação (ACK) do Kafka, o evento é marcado como processado no banco.

---

## 5. Resiliência: Resilience4j

O sistema foi blindado contra falhas parciais de infraestrutura:
- **Retry**: Aplicado no produtor do Kafka com backoff exponencial. Isso resolve falhas de rede momentâneas.
- **Circuit Breaker**: Monitora a taxa de erro do Kafka. Se o Kafka ficar offline por muito tempo, o circuito "abre", impedindo que novas tentativas de envio consumam recursos e permitindo que o sistema falhe graciosamente até que a infraestrutura se recupere.

---

## 6. Deep Dive: O Problema do Pool de Conexões (HikariCP)

Um dos momentos mais críticos do desenvolvimento foi o diagnóstico do "Connection Starvation".

### Análise do Deadlock de Pool
Ao utilizar `@Transactional` com `Propagation.REQUIRES_NEW` para a Auditoria, cada requisição solicitava **duas conexões** do pool Hikari simultaneamente.
- **Cenário de Falha**: Com 10 conexões no pool e 100 requisições simultâneas:
  1. As primeiras 10 requisições pegavam 1 conexão cada para iniciar a transferência.
  2. Cada uma dessas 10 threads pedia a **segunda conexão** para gravar o log de auditoria.
  3. O pool estava vazio. As threads ficavam esperando.
  4. As conexões originais não podiam ser liberadas até que o log terminasse.
  5. **Deadlock Completo**: O sistema travava até o timeout de 30 segundos.

### A Solução: Auditoria Assíncrona
Mudamos a auditoria para `@Async`. 
- Agora, a transação principal envia o pedido de log para um Executor e continua seu trabalho.
- O log é processado em sua própria thread e usa sua própria conexão em um momento diferente, garantindo que uma única requisição nunca precise "segurar" duas conexões do pool ao mesmo tempo.

---

## 7. Performance e Testes de Stress

O sistema foi submetido a baterias de testes rigorosas usando scripts Python personalizados:

- **Teste 1.000 requisições (50 concorrência)**: Processado em ~8s.
- **Teste 5.000 requisições (100 concorrência)**: Processado em ~29s (~170 reqs/seg).

**Observação de Performance**: Notamos que a performance por segundo aumentou proporcionalmente com a carga, provando que o gargalo não era o processamento Java, mas sim o tempo de espera de rede/disco (I/O Wait), que as Virtual Threads gerenciam com excelência.

---

## 8. Monitoramento e Saúde
Através do Spring Boot Actuator, expusemos endpoints críticos:
- `/actuator/health`: Status de saúde, incluindo Circuit Breaker.
- `/actuator/metrics`: Métricas detalhadas de latência e uso do pool Hikari.
- `/actuator/circuitbreakers`: Estado atual (CLOSED, OPEN, HALF_OPEN) das proteções do Kafka.

---

---

## 9. Registro de Evolução do Projeto

### ✅ Funcionalidades Implementadas
*Consolidado até Abril/2026*

- **Resiliência e Confiabilidade**: 
  - Implementação do padrão *Outbox* com garantia de atomicidade.
  - *Dead Letter Queue (DLQ)* para gestão de falhas persistentes em eventos.
  - *Circuit Breaker* e *Retry* com backoff exponencial via Resilience4j.
- **Segurança e Consistência**: 
  - Idempotência de ponta a ponta (API & Consumidor Kafka) usando Redis.
  - Bloqueios distribuídos via *Redisson* para evitar *race conditions*.
- **Escalabilidade e Infraestrutura**: 
  - Virtual Threads (Java 21) para alta vazão.
  - Migração de IDs para `UUID` e implementação de *Chave Primária Composta* (`id` + `transaction_date`) para habilitar o *Particionamento por Faixa* no banco de dados.

### 🔮 Roadmap de Evolução (Próximos Passos)
*Funcionalidades em planejamento ou estudo*

1. **Testes de Stress**: Executar bateria de testes de stress de 10.000 requisições (`stress_test.py`).
2. **Testcontainers**: Automatizar a suíte de testes de integração com infraestrutura real (Docker).
3. **Event Sourcing**: Estudo de viabilidade para migração de saldo atual para Event Sourcing (rastreabilidade).
4. **Particionamento de Banco**: Implementar particionamento por data na tabela `transactions`.
5. **Observabilidade Avançada**: Implementar tracing distribuído com Jaeger/OpenTelemetry.
