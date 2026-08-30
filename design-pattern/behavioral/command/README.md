# Command Pattern - Sistema de Tarefas Agendadas

Este projeto implementa o padrão Command para encapsular ações como criação, atualização e exclusão de tarefas, permitindo execução assíncrona e rollback de operações.

## Estrutura do Projeto

```
behavioral/command/
├── build.gradle.kts          # Configuração do Gradle
├── gradle.properties        # Propriedades do Gradle
├── settings.gradle.kts       # Configuração do settings
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── br/com/designpattern/command/
    │   │       ├── CommandApplication.kt  # Classe principal
    │   │       ├── controller/
    │   │       │   └── TaskController.kt     # Controlador REST
    │   │       ├── domain/
    │   │       │   ├── Task.kt               # Entidade Task
    │   │       │   ├── CommandResult.kt      # Resultado do comando
    │   │       │   ├── command/             # Comandos
    │   │       │   │   ├── Command.kt         # Interface Command
    │   │       │   │   ├── CreateTaskCommand.kt # Criação de tarefa
    │   │       │   │   ├── UpdateTaskStatusCommand.kt # Atualização de status
    │   │       │   │   └── DeleteTaskCommand.kt # Exclusão de tarefa
    │   │       │   └── TaskRepository.kt     # Repositório JPA
    │   │       └── service/
    │   │           └── CommandProcessor.kt  # Processador de comandos
    │   └── resources/
    │       └── application.properties      # Configurações da aplicação
    └── test/
        ├── kotlin/
        │   └── br/com/designpattern/command/
        │       ├── controller/
        │       │   └── TaskControllerTest.kt     # Testes do controlador
        │       ├── domain/command/
        │       │   ├── CreateTaskCommandTest.kt  # Testes de criação
        │       │   ├── UpdateTaskStatusCommandTest.kt # Testes de atualização
        │       │   └── DeleteTaskCommandTest.kt   # Testes de exclusão
        │       └── service/
        │           └── CommandProcessorTest.kt   # Testes do processador
        └── kotlin/br/com/designpattern/command/CommandApplicationTests.kt  # Testes básicos
```

## Padrões Aplicados

- **Command**: Encapsula ações como objetos para execução assíncrona e rollback.
- **Clean Code**: Métodos curtos, nomes descritivos e responsabilidades bem definidas.
- **Testes Unitários**: Cobertura completa dos comandos e serviço de processamento.

## Como Executar

1. Navegue até o diretório do projeto:
   ```bash
   cd behavioral/command
   ```

2. Execute o projeto com Gradle:
   ```bash
   ./gradlew bootRun
   ```

3. A API estará disponível em `http://localhost:8080`

## Endpoints

### Criar Tarefa
- **POST** `/api/tasks`
- **Request Body**:
  ```json
  {
    "name": "Process payment",
    "description": "Process customer payment for order #12345",
    "scheduledTime": "2024-08-09T10:00:00"
  }
  ```
- **Respostas**:
  - **200 OK**: Tarefa criada com sucesso
  - **400 BAD_REQUEST**: Falha na criação da tarefa

### Atualizar Status da Tarefa
- **PUT** `/api/tasks/{taskId}/status?status=IN_PROGRESS`
- **Respostas**:
  - **200 OK**: Status atualizado com sucesso
  - **400 BAD_REQUEST**: Falha na atualização do status

### Excluir Tarefa
- **DELETE** `/api/tasks/{taskId}`
- **Respostas**:
  - **200 OK**: Tarefa excluída com sucesso
  - **400 BAD_REQUEST**: Falha na exclusão da tarefa

### Desfazer Última Ação
- **POST** `/api/tasks/undo`
- **Respostas**:
  - **200 OK**: Última ação desfeita com sucesso
  - **400 BAD_REQUEST**: Nenhuma ação para desfazer

### Ver Histórico de Comandos
- **GET** `/api/tasks/history`
- **Resposta**:
  ```json
  {"commandCount": 3}
  ```

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
