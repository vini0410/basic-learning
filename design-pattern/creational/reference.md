# Padrões de Projeto Criacionais

Os padrões criacionais abstraem o processo de instanciação, tornando um sistema independente de como seus objetos são criados, compostos e representados.

## 1. Abstract Factory
Fornece uma interface para criar famílias de objetos relacionados ou dependentes sem especificar suas classes concretas. É útil quando o sistema deve ser independente de como seus produtos são criados e compostos.

## 2. Builder
Separa a construção de um objeto complexo da sua representação, de modo que o mesmo processo de construção possa criar diferentes representações. É ideal para objetos que possuem muitos parâmetros opcionais ou etapas de criação.

## 3. Factory Method
Define uma interface para criar um objeto, mas deixa as subclasses decidirem qual classe instanciar. O Factory Method permite que uma classe adie a instanciação para subclasses.

## 4. Prototype
Especifica os tipos de objetos a serem criados usando uma instância prototípica e cria novos objetos copiando esse protótipo. Permite criar novos objetos sem depender de suas classes específicas.

## 5. Singleton
Garante que uma classe tenha apenas uma instância e fornece um ponto global de acesso a ela. É útil quando precisamos de um único objeto para coordenar ações em todo o sistema (ex: conexão com banco de dados ou logger).

---

# Padrões Criacionais Complementares (Não-GoF)

Estes padrões não fazem parte da lista original do livro "Design Patterns" da Gang of Four (GoF), mas são amplamente utilizados no desenvolvimento de software moderno.

## 1. Dependency Injection (Injeção de Dependência)
Retira a responsabilidade de criar uma dependência da classe que a utiliza, fornecendo-a externamente (geralmente via construtor ou setter). Facilita o desacoplamento e a testabilidade do sistema.

## 2. Object Pool (Pool de Objetos)
Gerencia um conjunto de objetos inicializados e prontos para uso, em vez de criá-los e destruí-los sob demanda. É essencial quando o custo de criação de um objeto é alto (ex: conexões de banco de dados ou threads).

## 3. Simple Factory (Fábrica Simples)
Centraliza a lógica de criação de objetos em uma única classe ou método estático, retornando instâncias de diferentes tipos com base em um parâmetro. É uma versão simplificada do Factory Method.
