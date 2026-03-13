# Projetos para Padrões Comportamentais (Java/Kotlin Backend)

Estes projetos focam em como os objetos se comunicam e distribuem responsabilidades em processos de negócio.

## 1. Chain of Responsibility
**Projeto:** Pipeline de Validação de Pedidos.
- **Descrição:** Uma cadeia de validadores (SaldoCheck, EstoqueCheck, FraudeCheck, EnderecoCheck). Cada elo decide se o pedido pode continuar ou se deve ser rejeitado.
- **Objetivo:** Desacoplar as regras de validação e permitir reordená-las facilmente.

## 2. Command
**Projeto:** Sistema de Tarefas Agendadas ou Rollback de Transações.
- **Descrição:** Encapsular ações como `DeleteUserCommand` ou `RefundOrderCommand` que podem ser enfileiradas para processamento assíncrono ou desfeitas em caso de erro.
- **Objetivo:** Facilitar o agendamento de tarefas e o histórico de operações.

## 3. Interpreter
**Projeto:** Engine de Filtros Dinâmicos para Busca.
- **Descrição:** Criar uma gramática simples (ex: `price > 100 AND category == 'tech'`) que a API recebe como string e o backend interpreta para gerar uma query SQL ou Mongo.
- **Objetivo:** Dar flexibilidade total de busca para os usuários da API.

## 4. Iterator
**Projeto:** Paginação de Grandes Conjuntos de Dados.
- **Descrição:** Implementar um Iterator customizado que busca dados do banco em lotes (batch) de forma transparente para o consumidor, evitando carregar milhões de registros na memória.
- **Objetivo:** Processar grandes volumes de dados de forma eficiente.

## 5. Mediator
**Projeto:** Comunicação entre Microserviços/Módulos (CQRS).
- **Descrição:** Usar um Mediator (como o Spring ApplicationEventPublisher ou uma biblioteca como MediatR) para desacoplar Controllers de Services ou Services entre si.
- **Objetivo:** Reduzir o acoplamento direto; o Controller apenas envia uma "Intenção" e o Mediator encontra quem deve resolvê-la.

## 6. Memento
**Projeto:** Histórico de Alterações de Cadastro (Auditoria).
- **Descrição:** Salvar o estado anterior de uma entidade `User` ou `Product` antes de uma atualização para permitir visualizar o histórico ou restaurar uma versão anterior.
- **Objetivo:** Implementar auditoria ou "Desfazer" em sistemas administrativos.

## 7. Observer
**Projeto:** Sistema de Notificações Baseado em Eventos (Domain Events).
- **Descrição:** Quando um `Pedido` é pago, disparar um evento. Vários "Observers" (EmailService, EstoqueService, FiscalService) reagem a esse evento de forma independente.
- **Objetivo:** Garantir que novos comportamentos possam ser adicionados sem mudar o código de quem gerou o evento.

## 8. State
**Projeto:** Máquina de Estados de um Pedido ou Processo Seletivo.
- **Descrição:** Um Pedido que muda seu comportamento (permitir cancelar, permitir editar, calcular frete) dependendo se o estado é `AGUARDANDO_PAGAMENTO`, `PAGO` ou `ENVIADO`.
- **Objetivo:** Eliminar condicionais complexas (`if/else`) baseadas em status.

## 9. Strategy
**Projeto:** Motor de Promoções e Descontos.
- **Descrição:** Alternar algoritmos de desconto (Black Friday, Primeira Compra, Cliente VIP) dinamicamente com base no perfil do usuário ou data.
- **Objetivo:** Adicionar novas regras de negócio sem alterar o fluxo principal da API.

## 10. Template Method
**Projeto:** Importador de Dados Genérico.
- **Descrição:** Definir o esqueleto do processo: `AbrirArquivo()`, `ValidarFormato()`, `ProcessarLinhas()`, `SalvarNoBanco()`. As subclasses definem como ler cada formato (CSV, JSON, XML).
- **Objetivo:** Reutilizar a estrutura comum de processos e variar apenas os detalhes técnicos.

## 11. Visitor
**Projeto:** Gerador de Relatórios sobre Estruturas Complexas.
- **Descrição:** Usar um Visitor para percorrer uma árvore de categorias e produtos para calcular estatísticas de impostos ou gerar um resumo em XML/PDF.
- **Objetivo:** Adicionar novas operações sobre uma estrutura de objetos sem mudar as classes originais.

---

## Padrões Complementares

### 1. Null Object
**Projeto:** Guest User ou Resposta de Busca Vazia.
- **Descrição:** Retornar um objeto `GuestUser` em vez de `null` quando o usuário não está autenticado, evitando `NullPointerException`.

### 2. Specification
**Projeto:** Filtros Complexos de Repositório.
- **Descrição:** Combinar especificações como `IsActiveSpecification` + `HasPremiumPlanSpecification` para filtrar usuários no banco de dados de forma modular.
