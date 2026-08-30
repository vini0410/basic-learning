# Chain of Responsibility - Pipeline de Validação de Pedidos

Este projeto implementa o padrão Chain of Responsibility para validar pedidos em um sistema de e-commerce. A cadeia de validadores permite que cada elo decida se o pedido pode continuar ou deve ser rejeitado, desacoplando as regras de validação.

## Estrutura do Projeto

```
behavioral/chain-of-responsibility/
├── build.gradle.kts          # Configuração do Gradle
├── gradle.properties        # Propriedades do Gradle
├── settings.gradle.kts       # Configuração do settings
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── br/com/designpattern/chainofresponsibility/
    │   │       ├── ChainOfResponsibilityApplication.kt  # Classe principal
    │   │       ├── controller/
    │   │       │   └── OrderController.kt                # Controlador REST
    │   │       ├── domain/
    │   │       │   ├── Order.kt                          # Entidade Order
    │   │       │   ├── ValidationResult.kt               # Resultado da validação
    │   │       │   └── handler/                         # Handlers
    │   │       │       ├── BalanceCheckHandler.kt         # Verifica saldo
    │   │       │       ├── StockCheckHandler.kt           # Verifica estoque
    │   │       │       ├── FraudCheckHandler.kt            # Verifica fraude
    │   │       │       └── AddressCheckHandler.kt          # Verifica endereço
    │   │       └── service/
    │   │           └── OrderValidationService.kt        # Serviço de validação
    │   └── resources/
    │       └── application.properties                  # Configurações da aplicação
    └── test/
        ├── kotlin/
        │   └── br/com/designpattern/chainofresponsibility/
        │       ├── ChainOfResponsibilityApplicationTests.kt  # Testes básicos
        │       ├── controller/
        │       │   └── OrderControllerTest.kt                # Testes do controlador
        │       ├── domain/handler/
        │       │   ├── BalanceCheckHandlerTest.kt             # Testes de saldo
        │       │   ├── StockCheckHandlerTest.kt               # Testes de estoque
        │       │   ├── FraudCheckHandlerTest.kt                # Testes de fraude
        │       │   └── AddressCheckHandlerTest.kt              # Testes de endereço
        │       └── service/
        │           └── OrderValidationServiceTest.kt          # Testes do serviço
```

## Padrões Aplicados

- **Chain of Responsibility**: Cada handler decide se o pedido deve continuar na cadeia ou ser rejeitado.
- **Clean Code**: Métodos curtos, nomes descritivos e responsabilidades bem definidas.
- **Testes Unitários**: Cobertura completa dos handlers e serviço de validação.

## Como Executar

1. Navegue até o diretório do projeto:
   ```bash
   cd behavioral/chain-of-responsibility
   ```

2. Execute o projeto com Gradle:
   ```bash
   ./gradlew bootRun
   ```

3. A API estará disponível em `http://localhost:8080`

## Endpoints

### Criar Pedido
- **POST** `/api/orders`
- **Request Body**:
  ```json
  {
    "customerId": 1,
    "productId": 100,
    "quantity": 2,
    "totalAmount": "200.00"
  }
  ```
- **Respostas**:
  - **200 OK**: Pedido aprovado
  - **400 BAD_REQUEST**: Pedido rejeitado com mensagem de erro

## Testes

Execute os testes com:
```bash
./gradlew test
```

## Dependências Principais

- Spring Boot 3.2.5
- Kotlin 1.9.22
- H2 Database (banco em memória)
- MockK para mocks nos testes
- Jackson Module Kotlin para serialização
