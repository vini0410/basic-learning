# Padrões de Projeto Comportamentais

Os padrões comportamentais se preocupam com algoritmos e a atribuição de responsabilidades entre objetos. Eles não descrevem apenas padrões de objetos ou classes, mas também os padrões de comunicação entre eles.

## 1. Chain of Responsibility
Evita o acoplamento do remetente de uma solicitação ao seu receptor, dando a mais de um objeto a oportunidade de tratar a solicitação. Encadeia os objetos receptores e passa a solicitação ao longo da cadeia até que um objeto a trate.

## 2. Command
Encapsula uma solicitação como um objeto, permitindo parametrizar clientes com diferentes solicitações, enfileirar ou registrar solicitações e implementar operações que podem ser desfeitas.

## 3. Interpreter
Dada uma linguagem, define uma representação para sua gramática juntamente com um interpretador que usa a representação para interpretar sentenças na linguagem.

## 4. Iterator
Fornece uma maneira de acessar sequencialmente os elementos de um objeto agregado sem expor sua representação subjacente.

## 5. Mediator
Define um objeto que encapsula como um conjunto de objetos interage. O Mediator promove o acoplamento fraco ao evitar que os objetos se refiram uns aos outros explicitamente, permitindo variar suas interações de forma independente.

## 6. Memento
Sem violar o encapsulamento, captura e externaliza um estado interno de um objeto, de modo que o objeto possa ser restaurado para esse estado mais tarde.

## 7. Observer
Define uma dependência um-para-muitos entre objetos, de modo que, quando um objeto muda de estado, todos os seus dependentes são notificados e atualizados automaticamente.

## 8. State
Permite que um objeto altere seu comportamento quando seu estado interno muda. O objeto parecerá mudar de classe.

## 9. Strategy
Define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. O Strategy permite que o algoritmo varie independentemente dos clientes que o utilizam.

## 10. Template Method
Define o esqueleto de um algoritmo em uma operação, adiando alguns passos para as subclasses. O Template Method permite que as subclasses redefinam certos passos de um algoritmo sem alterar a estrutura do mesmo.

## 11. Visitor
Representa uma operação a ser executada nos elementos de uma estrutura de objetos. O Visitor permite definir uma nova operação sem mudar as classes dos elementos sobre os quais opera.

---

# Padrões Comportamentais Complementares (Não-GoF)

## 1. Null Object
Fornece um objeto como substituto para a falta de um objeto de um determinado tipo. O Null Object fornece um comportamento "fazer nada", eliminando a necessidade de verificações de nulidade constantes.

## 2. Specification (Especificação)
Permite a criação de regras de negócio reutilizáveis que podem ser combinadas usando lógica booleana. É útil para validar objetos ou selecioná-los de uma coleção.
