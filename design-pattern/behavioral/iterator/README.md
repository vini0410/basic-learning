# Iterator Design Pattern - Product Management API

This project demonstrates the **Iterator design pattern** using Kotlin and Spring Boot to manage product collections efficiently.

## Project Structure

```
src/
├── main/
│   ├── kotlin/br/com/designpattern/iterator/
│   │   ├── IteratorApp.kt          - Main application entry point
│   │   ├── controller/
│   │   │   └── ProductController.kt - REST endpoints for product management
│   │   ├── domain/
│   │   │   ├── Product.kt           - Product data class
│   │   │   ├── Iterator.kt          - Iterator interface
│   │   │   ├── ProductListIterator.kt - Concrete iterator implementation
│   │   │   └── ProductDatabase.kt    - Mock database with sample products
│   │   └── service/
│   │       └── ProductService.kt   - Business logic for product operations
│   └── resources/
│       └── application.properties - Application configuration
└── test/                         - Unit and integration tests
```

## Features

### 1. Product Management
- **Add Products**: POST `/api/products` with JSON payload
- **Batch Processing**: GET `/api/products/iterate` with optional parameters:
  - `batchSize`: Number of products per batch (default: 10)
  - `processAll`: Boolean to process all products in batches

### 2. Iterator Pattern Implementation
The project implements the Iterator pattern to traverse product collections:
- **ProductListIterator**: Concrete iterator that processes products in configurable batch sizes
- **Lazy Evaluation**: Products are fetched only when needed (hasNext/next)
- **State Management**: Tracks current position and batch boundaries

### 3. Batch Processing Strategy
Two approaches for processing products:
1. **Single Batch**: Returns one batch of products with metadata
2. **Process All**: Processes all products in sequential batches using callback

## API Endpoints

### Add a Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"Laptop","price":2999.99}'
```

### Iterate Products (Single Batch)
```bash
curl http://localhost:8080/api/products/iterate?batchSize=5
```

### Process All Products
```bash
curl http://localhost:8080/api/products/iterate?processAll=true&batchSize=3
```

## Building and Running

### Prerequisites
- Java 21+
- Kotlin 1.9.20
- Gradle 8.4+ (wrapped in project)

### Commands

**Build the application:**
```bash
./gradlew build
```

**Run the application:**
```bash
./gradlew bootRun
```

**Run tests:**
```bash
./gradlew test
```

## Design Pattern Explanation

The Iterator pattern decouples traversal logic from the collection:
- **Iterator Interface**: Defines `hasNext()` and `next()` methods
- **Concrete Iterator**: `ProductListIterator` implements batch processing
- **Aggregate**: Product list holds products and creates iterators
- **Client**: Controller uses iterator without knowing internal structure

## Benefits

1. **Simplified Client Code**: Clients work with iterators, not collection internals
2. **Multiple Traversals**: Same collection can be traversed differently (e.g., different batch sizes)
3. **Flexibility**: Easy to change iteration strategy without modifying client code
4. **Lazy Evaluation**: Products loaded only when needed, improving performance

## Sample Product Data

The application includes sample products in `ProductDatabase.kt`:
- Laptop ($2999.99)
- Smartphone ($1499.99)
- Headphones ($399.99)
- Keyboard ($99.99)
- Mouse ($59.99)

## Configuration

Application properties can be modified in `src/main/resources/application.properties`:
```properties
server.port=8080
spring.application.name=iterator-pattern
```
