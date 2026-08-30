import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.interpreter.study"])
class InterpreterApp

fun main(args: Array<String>) {
    runApplication<InterpreterApp>(*args)
}