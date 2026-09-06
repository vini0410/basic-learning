# Plano de Desenvolvimento - Memento Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Memento (Lembrança)
- **Caso de Uso:** Histórico de Alterações de Cadastro e Sistema de Rollback/Auditoria.
- **Objetivo:** Capturar e externalizar o estado interno de um objeto (ex: `UserProfile` ou `ProductConfiguration`) sem violar seu encapsulamento, permitindo restaurar estados anteriores a qualquer momento.

---

## 2. Especificações Técnicas

| Item | Tecnologia / Versão |
| :--- | :--- |
| **Linguagem** | Kotlin 1.9+ / Java 21 LTS |
| **Framework Principal** | Spring Boot 3.2+ |
| **Gerenciador de Dependências** | Gradle (Kotlin DSL - `build.gradle.kts`) |
| **Frameworks / Libraries Secundárias** | - `spring-boot-starter-web`<br>- `spring-boot-starter-data-jpa`<br>- `h2database` (Banco em memória)<br>- `io.github.microutils:kotlin-logging`<br>- `io.mockk:mockk` (Testes) |

---

## 3. Estrutura de Diretórios Solicitada

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/MementoApp`

```text
memento/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── MementoApp/
                ├── MementoApplication.kt
                ├── controller/
                │   └── UserProfileController.kt
                ├── domain/
                │   ├── UserProfile.kt (Originator)
                │   ├── UserProfileMemento.kt (Memento)
                │   └── UserProfileCaretaker.kt (Caretaker - Gerenciador do Histórico)
                ├── dto/
                │   ├── UpdateProfileRequest.kt
                │   └── ProfileHistoryResponse.kt
                └── service/
                    └── UserProfileService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Alteração de Estado (Originator):**
   - O `UserProfile` tem seus campos alterados (ex: nome, e-mail, permissões).
2. **Criação de Snapshot (Memento):**
   - Antes da alteração, chama-se `profile.createMemento()`, que gera um objeto imutável `UserProfileMemento` contendo a cópia do estado atual.
3. **Armazenamento no Caretaker:**
   - O `UserProfileCaretaker` guarda a pilha ou lista de mementos associados ao ID do perfil.
4. **Restauração (Undo/Rollback):**
   - Caso o usuário solicite desfazer uma alteração ou seja feita uma rejeição na auditoria, chama-se `profile.restore(memento)`.

---

## 5. Cenários de Teste

- **Imutabilidade do Memento:** Garantir que o estado interno guardado no `UserProfileMemento` não possa ser alterado externamente.
- **Teste de Rollback:** Testar alteração de N versões e desfazer (undo) retornando exatamente ao estado N-1 e estado original.
- **Teste REST:** Endpoints para atualizar cadastro, listar histórico de versões e restaurar versão específica via POST `/api/profiles/{id}/restore/{version}`.
