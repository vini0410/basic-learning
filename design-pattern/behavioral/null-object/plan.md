# Plano de Desenvolvimento - Null Object Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Null Object (Objeto Nulo - Padrão Comportamental Complementar)
- **Caso de Uso:** Representação de Usuário Anônimo (Guest User) e Resposta Neutra sem Nulos.
- **Objetivo:** Fornecer um objeto padrão com comportamento "fazer nada" ou com valores neutros/seguros em substituição à referência `null`. Elimina verificações constantes de nulidade (`if (user != null)`, `NullPointerException`) e simplifica o código do cliente.

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

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/NullObjectApp`

```text
null-object/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── NullObjectApp/
                ├── NullObjectApplication.kt
                ├── controller/
                │   └── UserAccountController.kt
                ├── domain/
                │   ├── User.kt (Interface ou Abstract Class)
                │   ├── AuthenticatedUser.kt (Implementação Real)
                │   ├── GuestUser.kt (Null Object)
                │   └── discount/
                │       ├── DiscountPolicy.kt (Interface de Políticas de Desconto)
                │       └── NoDiscountPolicy.kt (Null Object para Descontos)
                ├── repository/
                │   └── UserRepository.kt
                └── service/
                    └── UserService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Busca de Usuário:**
   - O `UserService` busca um usuário pelo token JWT ou ID.
2. **Retorno Seguro sem Null:**
   - Se o usuário não for encontrado ou a requisição for anônima, o repositório/serviço retorna uma instância de `GuestUser` em vez de `null`.
3. **Execução de Comportamentos:**
   - Chamadas como `user.getDiscountRate()`, `user.hasPermission("WRITE")`, `user.sendNotification(msg)` executam comportamento neutro ou seguro sem precisar de nenhum `if (user == null)`.
4. **Respostas da API:**
   - A API retorna dados seguros coerentes para o cliente sem quebrar com erro HTTP 500 (`NullPointerException`).

---

## 5. Cenários de Teste

- **Ausência de Checks de Nulo:** Garantir que o código de negócio chama todos os métodos de `User` sem verificar se a instância é nula.
- **Teste de Regras do GuestUser:** Validar se `GuestUser.hasPermission(...)` retorna sempre `false`, `getDiscountRate()` retorna `0.0`, e `isGuest()` retorna `true`.
- **Teste REST:** Chamada GET `/api/users/me` autenticada vs. anônima.
