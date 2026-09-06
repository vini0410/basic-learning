# Plano de Desenvolvimento - Specification Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Specification (Especificação - Padrão Comportamental Complementar)
- **Caso de Uso:** Engine de Regras de Negócio e Filtros Reutilizáveis de Clientes/Produtos.
- **Objetivo:** Encapsular regras de negócio (ex: *IsActive*, *HasPremiumPlan*, *HasOverduePayments*, *IsEligibleForLoan*) em objetos independentes e encadeá-los dinamicamente usando operadores lógicos booleanos (`AndSpecification`, `OrSpecification`, `NotSpecification`).

---

## 2. Especificações Técnicas

| Item | Tecnologia / Versão |
| :--- | :--- |
| **Linguagem** | Kotlin 1.9+ / Java 21 LTS |
| **Framework Principal** | Spring Boot 3.2+ |
| **Gerenciador de Dependências** | Gradle (Kotlin DSL - `build.gradle.kts`) |
| **Frameworks / Libraries Secundárias** | - `spring-boot-starter-web`<br>- `spring-boot-starter-data-jpa`<br>- `h2database` (Banco de Dados em memória)<br>- `com.fasterxml.jackson.module:jackson-module-kotlin`<br>- `io.github.microutils:kotlin-logging`<br>- `io.mockk:mockk` (Testes) |

---

## 3. Estrutura de Diretórios Solicitada

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/SpecificationApp`

```text
specification/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── SpecificationApp/
                ├── SpecificationApplication.kt
                ├── controller/
                │   └── CustomerEligibilityController.kt
                ├── domain/
                │   ├── Customer.kt
                │   └── specification/
                │       ├── Specification.kt (Interface funcional / classe abstrata)
                │       ├── AbstractSpecification.kt (Implementa e(), ou(), nao())
                │       ├── AndSpecification.kt
                │       ├── OrSpecification.kt
                │       ├── NotSpecification.kt
                │       ├── IsActiveCustomerSpecification.kt
                │       ├── HasPremiumPlanSpecification.kt
                │       └── HasCreditLimitSpecification.kt
                ├── dto/
                │   └── EligibilityCheckResponse.kt
                └── service/
                    └── CustomerEligibilityService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Combinação Modular de Regras:**
   - O `CustomerEligibilityService` combina especificações para formar a regra completa de elegibilidade a um empréstimo ou oferta:
     `val eligibleRule = IsActiveCustomerSpecification().and(HasPremiumPlanSpecification()).or(HasCreditLimitSpecification(10000.0))`
2. **Avaliação (In-Memory ou JPA Query):**
   - Para validar uma entidade individual: `eligibleRule.isSatisfiedBy(customer)`
3. **Reutilização de Regras:**
   - As especificações atômicas (`IsActiveCustomerSpecification`) podem ser reutilizadas em validações de cadastro, em filtros de busca de banco de dados ou em rotinas de envio de e-mail marketing.

---

## 5. Cenários de Teste

- **Testes das Operações Lógicas (`AND`, `OR`, `NOT`):** Garantir tabela-verdade correta para combinações de especificações.
- **Testes de Regras Atômicas:** Validar cada regra isoladamente contra entidades mockadas.
- **Teste REST:** Endpoint POST `/api/customers/{id}/check-eligibility` retornando boolean e a lista de regras violadas.
