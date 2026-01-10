import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KtlApp

fun main(args: Array<String>) {
    runApplication<KtlApp>(*args)
}