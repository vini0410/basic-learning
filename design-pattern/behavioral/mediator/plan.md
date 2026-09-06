# Plano de Desenvolvimento - Mediator Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Mediator (Mediador)
- **Caso de Uso:** Comunicação Desacoplada entre Módulos/Services em Arquitetura CQRS / Event-Driven.
- **Objetivo:** Evitar dependências diretas de injeção entre múltiplos serviços (ex: `OrderService` chamando diretamente `InventoryService`, `NotificationService`, `BillingService`). O `Mediator` centraliza o roteamento de comandos/intenções.

---

## 2. Especificações Técnicas

| Item | Tecnologia / Versão |
| :--- | :--- |
| **Linguagem** | Kotlin 1.9+ / Java 21 LTS |
| **Framework Principal** | Spring Boot 3.2+ |
| **Gerenciador de Dependências** | Gradle (Kotlin DSL - `build.gradle.kts`) |
| **Frameworks / Libraries Secundárias** | - `spring-boot-starter-web`<br>- `com.fasterxml.jackson.module:jackson-module-kotlin`<br>- `io.github.microutils:kotlin-logging`<br>- `io.mockk:mockk` (Testes)<br>- `org.junit.jupiter:junit-jupiter` |

---

## 3. Estrutura de Diretórios Solicitada

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/MediatorApp`

```text
mediator/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── MediatorApp/
                ├── MediatorApplication.kt
                ├── controller/
                │   └── OrderCommandController.kt
                ├── domain/
                │   ├── command/
                │   │   ├── CreateOrderCommand.kt
                │   │   └── CancelOrderCommand.kt
                │   └── mediator/
                │       ├── CommandMediator.kt (Interface do Mediador)
                │       ├── CommandHandler.kt (Interface dos Manipuladores)
                │       └── InMemoryCommandBusMediator.kt (Implementação do Mediador)
                ├── handler/
                │   ├── CreateOrderHandler.kt
                │   └── CancelOrderHandler.kt
                └── service/
                    ├── InventoryService.kt
                    └── NotificationService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Recebimento de Solicitação REST:**
   - O `OrderCommandController` recebe uma requisição HTTP POST para criar um pedido.
2. **Envio ao Mediador:**
   - Em vez de chamar N serviços diretamente, o Controller instancia um `CreateOrderCommand` e o envia para o `CommandMediator.send(command)`.
3. **Resolução e Execução:**
   - O `CommandMediator` busca o `CommandHandler` registrado para aquele tipo de comando e executa a regra de negócio centralizada.
4. **Desacoplamento:**
   - Adicionar novos handlers ou modificar o fluxo não impacta o Controller nem os outros componentes.

---

## 5. Cenários de Teste

- **Teste Unitário do Mediador:** Garantir que comandos sem handlers registrados lancem exceção adequada.
- **Teste Unitário dos Handlers:** Validar a execução isolada de cada handler.
- **Teste de Integração REST:** Enviar comando via endpoint POST `/api/orders` e verificar status `200 OK` / `202 Accepted`.
