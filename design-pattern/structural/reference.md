# Padrões de Projeto Estruturais

Os padrões estruturais explicam como montar objetos e classes em estruturas maiores, mantendo-as flexíveis e eficientes.

## 1. Adapter
Converte a interface de uma classe em outra interface que os clientes esperam. O Adapter permite que classes trabalhem juntas quando isso de outra forma seria impossível devido a interfaces incompatíveis.

## 2. Bridge
Separa uma abstração da sua implementação, permitindo que as duas possam variar independentemente. É útil para evitar uma explosão de subclasses ao lidar com diferentes tipos de implementações.

## 3. Composite
Compõe objetos em estruturas de árvore para representar hierarquias parte-todo. O Composite permite que os clientes tratem objetos individuais e composições de objetos de maneira uniforme.

## 4. Decorator
Anexa responsabilidades adicionais a um objeto dinamicamente. Os Decorators fornecem uma alternativa flexível à herança para estender funcionalidades.

## 5. Facade
Fornece uma interface unificada para um conjunto de interfaces em um subsistema. A Facade define uma interface de nível superior que torna o subsistema mais fácil de usar.

## 6. Flyweight
Usa o compartilhamento para suportar grandes quantidades de objetos de grão fino de forma eficiente. É ideal para reduzir o uso de memória em sistemas com muitos objetos semelhantes.

## 7. Proxy
Fornece um substituto ou um espaço reservado para outro objeto para controlar o acesso a ele. Pode ser usado para carregamento preguiçoso, controle de acesso ou logging.

---

# Padrões Estruturais Complementares (Não-GoF)

## 1. Data Transfer Object (DTO)
Um objeto que transporta dados entre processos para reduzir o número de chamadas de método em uma interface remota. É puramente um recipiente para dados, sem comportamento.

## 2. Extension Object
Permite que o comportamento de um objeto seja estendido sem alterar sua classe original, através da adição de "interfaces de extensão". É útil em sistemas altamente dinâmicos.
