# Plano de Desenvolvimento - Template Method Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Template Method (Método Modelo)
- **Caso de Uso:** Importador de Dados Genérico e Multiformato (CSV, JSON, XML).
- **Objetivo:** Definir o esqueleto do algoritmo de importação de arquivos em um método concreto na classe abstrata (ex: `openFile() -> parseContent() -> validateData() -> saveData() -> logMetrics()`), permitindo que as subclasses redefinam apenas etapas específicas (como a leitura/parse de cada formato).

---

## 2. Especificações Técnicas

| Item | Tecnologia / Versão |
| :--- | :--- |
| **Linguagem** | Kotlin 1.9+ / Java 21 LTS |
| **Framework Principal** | Spring Boot 3.2+ / Java Standalone |
| **Gerenciador de Dependências** | Gradle (Kotlin DSL - `build.gradle.kts`) |
| **Frameworks / Libraries Secundárias** | - `spring-boot-starter-web`<br>- `com.fasterxml.jackson.module:jackson-module-kotlin`<br>- `com.fasterxml.jackson.dataformat:jackson-dataformat-xml`<br>- `io.github.microutils:kotlin-logging`<br>- `io.mockk:mockk` (Testes) |

---

## 3. Estrutura de Diretórios Solicitada

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/TemplateMethodApp`

```text
template-method/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── TemplateMethodApp/
                ├── TemplateMethodApplication.kt
                ├── controller/
                │   └── DataImportController.kt
                ├── domain/
                │   ├── ImportedRecord.kt
                │   └── importer/
                │       ├── DataImporterTemplate.kt (Classe Abstrata com o Template Method)
                │       ├── CsvDataImporter.kt
                │       ├── JsonDataImporter.kt
                │       └── XmlDataImporter.kt
                ├── dto/
                │   └── ImportSummaryDto.kt
                └── service/
                    └── DataImportService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Upload / Leitura do Arquivo:**
   - O `DataImportController` recebe o arquivo e o tipo (CSV, JSON ou XML).
2. **Execução do Template Method:**
   - O `DataImportService` chama o método modelo `importer.importData(fileInput)`.
3. **Passos Fixos vs. Passos Flexíveis:**
   - **Passos Fixos (definidos no Template):** `openFile()`, `validateData()`, `saveData()`, `sendNotification()`.
   - **Passo Flexível (sobrescrito nas subclasses):** `parseContent(inputStream)` -> O `CsvDataImporter` lê linhas por vírgula, o `JsonDataImporter` lê arvore JSON, etc.
4. **Hooks Opcionais:**
   - Métodos "hook" como `shouldAudit(): Boolean` com implementação padrão que podem ser sobrescritos se necessário.

---

## 5. Cenários de Teste

- **Garantia de Fluxo:** Testar se a ordem de execução dos passos (`open` -> `parse` -> `validate` -> `save`) é mantida rigorosamente.
- **Testes por Formato:** Testar a importação correta de um payload CSV, de um JSON e de um XML.
- **Teste de Falha em Validação:** Garantir que se a etapa comum `validateData()` falhar, a etapa `saveData()` não seja invocada.
