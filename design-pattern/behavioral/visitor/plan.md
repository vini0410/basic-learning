# Plano de Desenvolvimento - Visitor Pattern

## 1. Visão Geral do Projeto
- **Padrão:** Visitor (Visitante)
- **Caso de Uso:** Gerador de Relatórios e Cálculos sobre Estruturas Complexas de Produtos / Categorias (Hierarquia em Árvore).
- **Objetivo:** Separar um algoritmo da estrutura de objetos sobre a qual opera. Permite adicionar novas operações (ex: *Cálculo de Impostos*, *Exportação para XML/PDF*, *Auditoria de Estoque*) sobre a hierarquia de `Product` e `Category` sem modificar as classes de domínio originais.

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

> **Ressalva Obrigatória:** Todo o código fonte principal da aplicação deve residir no caminho: `src/com/study/VisitorApp`

```text
visitor/
├── build.gradle.kts
├── settings.gradle.kts
├── plan.md
└── src/
    └── com/
        └── study/
            └── VisitorApp/
                ├── VisitorApplication.kt
                ├── controller/
                │   └── CatalogReportController.kt
                ├── domain/
                │   ├── element/
                │   │   ├── CatalogElement.kt (Interface aceita o Visitor)
                │   │   ├── PhysicalProduct.kt
                │   │   ├── DigitalProduct.kt
                │   │   └── ProductCategory.kt (Contém lista de CatalogElement)
                │   └── visitor/
                │       ├── CatalogVisitor.kt (Interface do Visitante)
                │       ├── TaxCalculationVisitor.kt
                │       ├── XmlExportVisitor.kt
                │       └── StockSummaryVisitor.kt
                ├── dto/
                │   └── ReportResponseDto.kt
                └── service/
                    └── CatalogReportService.kt
```

---

## 4. Fluxo da Aplicação e Casos de Uso

1. **Construção da Árvore de Catálogo:**
   - O `CatalogReportService` recupera uma categoria contendo produtos físicos, produtos digitais e subcategorias.
2. **Duplo Despacho (Double Dispatch):**
   - Ao chamar `element.accept(visitor)`, a classe concreta (ex: `PhysicalProduct`) invoca `visitor.visitPhysicalProduct(this)`.
3. **Processamento Especializado:**
   - O `TaxCalculationVisitor` aplica regras tributárias distintas para produtos físicos (ICMS/Frete) vs. produtos digitais (ISS/Sem frete).
4. **Adição de Novas Operações:**
   - Para adicionar um relatório de descontos em massa, cria-se apenas a classe `MassDiscountVisitor`, sem alterar `PhysicalProduct` ou `DigitalProduct`.

---

## 5. Cenários de Teste

- **Teste de Duplo Despacho:** Garantir que o visitante correto é chamado para cada tipo de elemento no catálogo.
- **Teste de Travessia da Árvore:** Garantir que a `ProductCategory` propaga a visita aos seus elementos filhos.
- **Teste de Cálculos Complexos:** Verificar se a soma total de impostos do `TaxCalculationVisitor` bate com o esperado para uma árvore com N produtos físicos e digitais.
- **Teste REST:** Endpoint GET `/api/catalog/reports/tax` e GET `/api/catalog/reports/xml`.
