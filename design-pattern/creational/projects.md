# Projetos para Padrões Criacionais (Java/Kotlin Backend)

Estes projetos focam na criação controlada de objetos em ambientes de APIs REST.

## 1. Abstract Factory
**Projeto:** Sistema de Notificação Multi-Canal.
- **Descrição:** Uma fábrica que cria conjuntos de componentes para notificações (Email, SMS, Push). Cada família (ex: Email) tem seu próprio "Sender", "TemplateFormatter" e "ErrorLogger".
- **Objetivo:** Alternar entre provedores de comunicação sem mudar o código que envia a mensagem.

## 2. Builder
**Projeto:** Fluent API para Construção de Relatórios/Documentos.
- **Descrição:** Um builder para criar objetos `ReportConfiguration` que possuem dezenas de filtros opcionais, colunas de ordenação e formatos de saída (PDF, Excel, JSON).
- **Objetivo:** Evitar construtores gigantes e tornar a criação de objetos de configuração legível.

## 3. Factory Method
**Projeto:** Processador de Pagamentos Plugável.
- **Descrição:** Uma classe base `PaymentProcessor` que define o fluxo, mas delega a criação do cliente específico (Stripe, PayPal, Cielo) para subclasses ou fábricas específicas baseadas no tipo de pagamento recebido na API.
- **Objetivo:** Facilitar a adição de novos métodos de pagamento sem alterar o controller.

## 4. Prototype
**Projeto:** Templates de Campanhas de Marketing.
- **Descrição:** Clonar uma "Campanha Base" (com todas as configurações de público e data) para criar variações rápidas (A/B Testing) sem precisar buscar tudo do banco de dados novamente.
- **Objetivo:** Reduzir o custo de criação de objetos complexos que já existem em memória.

## 5. Singleton
**Projeto:** Cache Manager ou Token Store.
- **Descrição:** Garantir que exista apenas uma instância do gerenciador de cache em memória (ex: Caffeine ou um wrapper de Redis) para toda a aplicação.
- **Objetivo:** Centralizar o acesso a recursos globais e economizar memória.

---

## Padrões Complementares

### 1. Dependency Injection (Injeção de Dependência)
**Projeto:** Configuração de Clientes de API Externas.
- **Descrição:** Usar Spring/Koin para injetar implementações de `StorageService` (LocalFileSystem em dev, S3 em prod).

### 2. Object Pool
**Projeto:** Pool de Clientes HTTP.
- **Descrição:** Implementar ou configurar um pool de conexões para chamadas externas para evitar o overhead de abertura de sockets TCP em cada request.
