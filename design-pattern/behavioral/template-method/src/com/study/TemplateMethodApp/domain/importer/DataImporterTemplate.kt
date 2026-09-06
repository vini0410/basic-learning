package com.study.TemplateMethodApp.domain.importer

import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

data class ImportRecord(
    val code: String,
    val name: String,
    val price: Double
)

data class ImportResult(
    val format: String,
    val totalRecords: Int,
    val records: List<ImportRecord>,
    val status: String
)

/**
 * Classe Abstrata que define o Template Method.
 */
abstract class DataImporterTemplate {

    /**
     * O Template Method: Define o esqueleto fixo do algoritmo de importacao.
     * Declara o fluxo final (final method em Java, nao aberto em Kotlin).
     */
    fun processImport(rawContent: String): ImportResult {
        logger.info { "Iniciando processo de importacao no formato: ${getFormatName()}" }

        // Step 1: Validacao comum de entrada bruta
        validateRawInput(rawContent)

        // Step 2: Passo primitivo (abstrato) - cada subclasse sabe como converter seu formato
        val records = parseRecords(rawContent)

        // Step 3: Validacao comum dos registros extraidos
        val validRecords = validateRecords(records)

        // Step 4: Salvamento comum dos registros
        saveToDatabase(validRecords)

        // Step 5: Hook opcional
        onSuccessHook(validRecords)

        return ImportResult(
            format = getFormatName(),
            totalRecords = validRecords.size,
            records = validRecords,
            status = "SUCCESS"
        )
    }

    protected abstract fun getFormatName(): String

    /**
     * Passo primitivo que DEVE ser implementado pelas subclasses.
     */
    protected abstract fun parseRecords(rawContent: String): List<ImportRecord>

    /**
     * Passo fixo comum a todas as importacoes.
     */
    private fun validateRawInput(rawContent: String) {
        require(rawContent.isNotBlank()) { "Conteudo de importacao nao pode ser em branco" }
    }

    /**
     * Passo fixo comum de regras de negocio dos registros.
     */
    private fun validateRecords(records: List<ImportRecord>): List<ImportRecord> {
        require(records.isNotEmpty()) { "Nenhum registro valido foi encontrado para importacao" }
        return records.filter { it.price >= 0 }
    }

    private fun saveToDatabase(records: List<ImportRecord>) {
        logger.info { "Persistindo ${records.size} registros no banco de dados..." }
    }

    /**
     * Hook Method: Pode ser sobrescrito por subclasses caso queiram executar logicas pos-sucesso.
     */
    protected open fun onSuccessHook(records: List<ImportRecord>) {
        // Implementacao padrao vazia (Hook)
    }
}

@Component
class CsvDataImporter : DataImporterTemplate() {
    override fun getFormatName(): String = "CSV"

    override fun parseRecords(rawContent: String): List<ImportRecord> {
        // Formato esperado: CODE,NAME,PRICE por linha
        return rawContent.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { line ->
                val parts = line.split(",")
                require(parts.size >= 3) { "Linha CSV invalida: $line" }
                ImportRecord(
                    code = parts[0].trim(),
                    name = parts[1].trim(),
                    price = parts[2].trim().toDouble()
                )
            }
    }
}

@Component
class JsonDataImporter : DataImporterTemplate() {
    override fun getFormatName(): String = "JSON"

    override fun parseRecords(rawContent: String): List<ImportRecord> {
        // Exemplo simplificado de parsing de pares JSON simples
        require(rawContent.startsWith("[") && rawContent.endsWith("]")) { "JSON de importacao deve ser um Array []" }
        // Para testes simples ou mocks: simula extracao por regex ou split
        val itemMatches = Regex("""\{"code":"(.*?)","name":"(.*?)","price":(.*?)\}""").findAll(rawContent)
        return itemMatches.map { match ->
            ImportRecord(
                code = match.groupValues[1],
                name = match.groupValues[2],
                price = match.groupValues[3].toDouble()
            )
        }.toList()
    }
}

@Component
class XmlDataImporter : DataImporterTemplate() {
    override fun getFormatName(): String = "XML"

    override fun parseRecords(rawContent: String): List<ImportRecord> {
        require(rawContent.contains("<items>") && rawContent.contains("</items>")) { "XML invalido" }
        val itemMatches = Regex("""<item><code>(.*?)</code><name>(.*?)</name><price>(.*?)</price></item>""").findAll(rawContent)
        return itemMatches.map { match ->
            ImportRecord(
                code = match.groupValues[1],
                name = match.groupValues[2],
                price = match.groupValues[3].toDouble()
            )
        }.toList()
    }
}
