# Plano de Desenvolvimento - State Pattern

## 1. Visão Geral do Projeto
- **Padrão:** State (Estado)
- **Caso de Uso:** Máquina de Estados de Pedido de E-Commerce ou Processo Seletivo.
- **Objetivo:** Permite que um objeto altere seu comportamento quando seu estado interno muda. Substitui blocos extensos e frágeis de condicionais (`if/else` ou `switch/case` baseados em enums de status) por classes de Estado dedicadas.

---

## 2. Especificações Técnicas

| Item | Tecnologia / Versão |
| :--- | :--- |
| **Linguagem** | Kotlin 1.9+ / Java 21 LTS |
| **Framework Principal** | Spring Boot 3.2+ |
| **Gerenciador de Dependências** | Gradle (Kotlin DSL - `build.gradle.kts`) |
| **Frameworks / Libraries Secundárias** | - `spring-boot-starter-web`<br>- `com.fasterxml.jackson.module:jackson-module-kotlin`<br>- `io.github.microutils:kotlin-logging`<br>- `io.mockk:mockk` (Testes) |

---

## 3. Estrutura de Diretórios Solicitada

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/StateApp`

```text
state/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── StateApp/
                ├── StateApplication.kt
                ├── controller/
                │   └── OrderStateController.kt
                ├── domain/
                │   ├── OrderContext.kt (Contexto que armazena o estado atual)
                │   └── state/
                │       ├── OrderState.kt (Interface / Sealed Class do Estado)
                │       ├── PendingPaymentState.kt
                │       ├── PaidState.kt
                │       ├── ShippedState.kt
                │       ├── DeliveredState.kt
                │       └── CancelledState.kt
                ├── dto/
                │   └── OrderStatusResponse.kt
                └── service/
                    └── OrderLifecycleService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Ciclo de Vida do Pedido (Transição de Estados):**
   - Um pedido inicia no estado `PendingPaymentState`.
2. **Ações sobre o Estado:**
   - Métodos como `pay()`, `ship()`, `cancel()`, `deliver()` são chamados no `OrderContext`.
3. **Delegação da Regra:**
   - O `OrderContext` delega a execução para o estado atual: `currentState.pay(this)`.
4. **Validação Transicional:**
   - Se o pedido está em `ShippedState` e o usuário tenta chamar `cancel()`, a própria classe `ShippedState` lança uma exceção `IllegalStateException("Não é possível cancelar um pedido já enviado")`.
   - Se a ação for válida, o estado faz a transição trocando o `currentState` no `OrderContext`.

---

## 5. Cenários de Teste

- **Testes de Transição Válida:** Validar transição em cadeia: `PendingPaymentState` -> `PaidState` -> `ShippedState` -> `DeliveredState`.
- **Testes de Ações Inválidas:** Garantir exceções apropriadas ao tentar transições proibidas (ex: tentar pagar um pedido cancelado ou cancelar um entregue).
- **Teste REST:** Endpoints POST `/api/orders/{id}/pay`, POST `/api/orders/{id}/ship`, etc.
