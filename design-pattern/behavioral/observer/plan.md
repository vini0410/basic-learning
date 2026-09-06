# Plano de Desenvolvimento - Observer Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Observer (Observador / Domain Events)
- **Caso de Uso:** Sistema de Notificações Baseado em Eventos de Domínio no E-Commerce.
- **Objetivo:** Notificar automaticamente múltiplos serviços interessados (ex: `EmailService`, `InventoryService`, `AuditService`) sempre que o estado do `Pedido` mudar para `PAGO`, sem acoplar o serviço de pedidos a cada um desses receptores.

---

## 2. Especificações Técnicas

| Item | Tecnologia / Versão |
| :--- | :--- |
| **Linguagem** | Kotlin 1.9+ / Java 21 LTS |
| **Framework Principal** | Spring Boot 3.2+ |
| **Gerenciador de Dependências** | Gradle (Kotlin DSL - `build.gradle.kts`) |
| **Frameworks / Libraries Secundárias** | - `spring-boot-starter-web`<br>- Spring `ApplicationEventPublisher` & `@EventListener`<br>- `com.fasterxml.jackson.module:jackson-module-kotlin`<br>- `io.github.microutils:kotlin-logging`<br>- `io.mockk:mockk` (Testes) |

---

## 3. Estrutura de Diretórios Solicitada

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/ObserverApp`

```text
observer/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── ObserverApp/
                ├── ObserverApplication.kt
                ├── controller/
                │   └── PaymentController.kt
                ├── domain/
                │   ├── event/
                │   │   ├── OrderPaidEvent.kt
                │   │   └── DomainEvent.kt
                │   └── observer/
                │       ├── OrderObserver.kt (Interface customizada ou Spring Listener)
                │       ├── EmailNotificationObserver.kt
                │       ├── InventoryUpdateObserver.kt
                │       └── FiscalInvoiceObserver.kt
                ├── publisher/
                │   └── OrderEventPublisher.kt (Subject / Emissor de Eventos)
                └── service/
                    └── PaymentProcessingService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Confirmação de Pagamento:**
   - O `PaymentController` recebe a notificação de confirmação de pagamento.
2. **Disparo do Evento (Subject):**
   - O `PaymentProcessingService` atualiza o pedido e invoca o `OrderEventPublisher` para publicar o `OrderPaidEvent`.
3. **Notificação Assíncrona / Síncrona dos Observers:**
   - Todos os Observers inscritos (`EmailNotificationObserver`, `InventoryUpdateObserver`, `FiscalInvoiceObserver`) reagem individualmente ao evento.
4. **Extensibilidade:**
   - Para adicionar um novo comportamento (ex: Enviar SMS ou integrar com CRM), basta criar uma nova classe Observer sem alterar o código do `PaymentProcessingService`.

---

## 5. Cenários de Teste

- **Inscrição e Notificação dos Observers:** Garantir que ao disparar 1 evento, N observadores sejam invocados com os dados exatos do evento.
- **Isolamento de Falhas:** Testar se a falha em um observer específico (ex: erro no envio de e-mail) não impede a execução dos demais observadores nem quebra a transação principal caso esteja configurado assincronamente.
- **Teste REST:** Endpoint POST `/api/payments/confirm` disparando todo o pipeline de notificações observáveis.
