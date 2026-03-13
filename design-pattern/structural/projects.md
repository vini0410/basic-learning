# Projetos para Padrões Estruturais (Java/Kotlin Backend)

Estes projetos focam na composição de classes e objetos para formar estruturas maiores e flexíveis em APIs REST.

## 1. Adapter
**Projeto:** Wrapper para API Legada (XML para JSON).
- **Descrição:** Criar um Adapter que consome um serviço SOAP antigo em XML e expõe uma interface moderna em JSON/REST para o resto da aplicação.
- **Objetivo:** Permitir que novos sistemas usem tecnologias legadas sem precisar lidar com formatos antigos.

## 2. Bridge
**Projeto:** Exportador de Relatórios Multi-Formato e Multi-Destino.
- **Descrição:** Separar a abstração do relatório (Vendas, Usuários) da sua implementação de saída (PDF, Excel, CSV) e do seu destino (S3, Local, FTP).
- **Objetivo:** Evitar a explosão de classes como `VendasPDFnoS3`, `VendasExcelnoLocal`, etc.

## 3. Composite
**Projeto:** Hierarquia de Departamentos e Organização.
- **Descrição:** Representar a estrutura de uma empresa onde um "Departamento" pode conter "Funcionários" ou "Sub-departamentos", tratando ambos como um `OrganizacaoItem`.
- **Objetivo:** Calcular salários ou listar membros de forma recursiva e transparente.

## 4. Decorator
**Projeto:** Camadas de Resiliência e Logging em APIs.
- **Descrição:** Envolver um `ExternalService` base com decoradores para Adicionar Logging, Caching (Redis), e Retry (Resilience4j).
- **Objetivo:** Estender o comportamento de serviços de terceiros sem alterar o código original ou usar herança excessiva.

## 5. Facade
**Projeto:** Orquestrador de Checkout (Order Facade).
- **Descrição:** Uma única classe `CheckoutFacade` que simplifica o processo de compra chamando o `StockService`, `PaymentService`, `ShippingService` e `NotificationService`.
- **Objetivo:** Oferecer uma API simplificada para o Controller, escondendo a complexidade interna dos subsistemas.

## 6. Flyweight
**Projeto:** Sistema de Gerenciamento de Permissões/Roles.
- **Descrição:** Em uma aplicação com milhares de usuários, compartilhar instâncias únicas de objetos `Role` (ADMIN, USER, GUEST) em vez de criar um novo objeto para cada usuário.
- **Objetivo:** Reduzir drasticamente o consumo de memória em sessões simultâneas.

## 7. Proxy
**Projeto:** Proteção de Recursos (Rate Limiter ou Security Proxy).
- **Descrição:** Um Proxy que intercepta chamadas a um serviço sensível para verificar permissões ou limitar o número de chamadas por minuto (Rate Limiting).
- **Objetivo:** Controlar o acesso e adicionar segurança sem poluir a lógica de negócio do serviço real.

---

## Padrões Complementares

### 1. Data Transfer Object (DTO)
**Projeto:** Mapeamento de Entidades para Respostas de API.
- **Descrição:** Usar DTOs para retornar apenas os campos necessários ao cliente, evitando expor senhas ou IDs internos do banco de dados.

### 2. Extension Object
**Projeto:** Sistema de Plugins para um CMS.
- **Descrição:** Permitir que funcionalidades extras sejam "anexadas" a um objeto `Post` sem alterar sua classe (ex: plugin de SEO, plugin de comentários).
