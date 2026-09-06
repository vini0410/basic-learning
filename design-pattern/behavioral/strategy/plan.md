# Plano de Desenvolvimento - Strategy Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Strategy (Estratégia)
- **Caso de Uso:** Motor de Promoções e Cálculo de Descontos Dinâmicos.
- **Objetivo:** Definir uma família de algoritmos de desconto (ex: *Black Friday*, *Desconto por Valor Mínimo*, *Cliente VIP*, *Primeira Compra*), encapsular cada um deles em classes separadas e torná-los intercambiáveis em tempo de execução com base no contexto do carrinho ou do cliente.

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

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/StrategyApp`

```text
strategy/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── StrategyApp/
                ├── StrategyApplication.kt
                ├── controller/
                │   └── CheckoutController.kt
                ├── domain/
                │   ├── Cart.kt
                │   ├── CustomerType.kt
                │   └── strategy/
                │       ├── DiscountStrategy.kt (Interface da Estratégia)
                │       ├── BlackFridayDiscountStrategy.kt
                │       ├── VipCustomerDiscountStrategy.kt
                │       ├── FirstPurchaseDiscountStrategy.kt
                │       └── NoDiscountStrategy.kt
                ├── dto/
                │   ├── CheckoutRequest.kt
                │   └── CheckoutResponse.kt
                ├── factory/
                │   └── DiscountStrategyFactory.kt (Seleção da Strategy)
                └── service/
                    └── CheckoutService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Requisição de Checkout:**
   - O `CheckoutController` recebe os itens do carrinho e o cupom/perfil do cliente.
2. **Seleção Dinâmica de Estratégia:**
   - O `DiscountStrategyFactory` identifica qual estratégia de desconto deve ser aplicada (ou combina estratégias).
3. **Cálculo Desacoplado:**
   - O `CheckoutService` executa `strategy.calculateDiscount(cart)`, sem precisar saber detalhes da fórmula interna de cada desconto.
4. **Adição de Novas Regras:**
   - Adicionar uma promoção de Natal envolve apenas criar uma nova classe que implementa `DiscountStrategy`, respeitando o Princípio do Aberto/Fechado (OCP).

---

## 5. Cenários de Teste

- **Testes Unitários por Estratégia:** Testar o cálculo individual de cada classe `DiscountStrategy` com valores de borda (ex: carrinho zerado, valor abaixo do mínimo para desconto).
- **Teste de Seleção da Fábrica:** Garantir que a factory retorna a estratégia correta para cada perfil/cupom.
- **Teste REST:** Simulações de checkout via POST `/api/checkout/calculate` enviando cupons e perfis diferentes.
