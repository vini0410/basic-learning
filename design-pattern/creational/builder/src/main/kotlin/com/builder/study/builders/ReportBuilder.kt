package com.builder.study.builders

import com.builder.study.exceptions.ErrorType
import com.builder.study.exceptions.ReportBuilderException
import com.builder.study.models.*
import io.ktor.http.*

/**
 * Interface pública do Builder.
 */
interface ReportBuilder {
    fun setTitle(title: String): ReportBuilder
    fun setOutputFormat(format: OutputFormat): ReportBuilder
    fun addFilter(field: String, operator: String, value: String): ReportBuilder
    fun addSorting(field: String, direction: SortDirection = SortDirection.ASC): ReportBuilder
    fun setIncludeHeader(include: Boolean): ReportBuilder
    fun setIncludeFooter(include: Boolean): ReportBuilder
    fun setItemsPerPage(items: Int): ReportBuilder
    fun build(): ReportConfiguration
}

/**
 * Implementação privada para garantir o encapsulamento.
 * O mundo externo só acessa via a função 'report' ou métodos de fábrica.
 */
private class DefaultReportBuilder : ReportBuilder {
    private var title: String = ""
    private var format: OutputFormat = OutputFormat.PDF
    private val filters = mutableListOf<Filter>()
    private val sorting = mutableListOf<SortColumn>()
    private var includeHeader: Boolean = true
    private var includeFooter: Boolean = true
    private var itemsPerPage: Int = 50

    override fun setTitle(title: String): ReportBuilder {
        if (title.isBlank()) {
            throw ReportBuilderException(
                "O título do relatório não pode estar vazio.",
                type = ErrorType.VALIDATION_ERROR,
                errorCode = "REPORT_TITLE_REQUIRED",
                httpStatus = HttpStatusCode.UnprocessableEntity
            )
        }
        this.title = title
        return this
    }

    override fun setOutputFormat(format: OutputFormat): ReportBuilder {
        this.format = format
        return this
    }

    override fun addFilter(field: String, operator: String, value: String): ReportBuilder {
        if (field.isBlank()) {
            throw ReportBuilderException(
                "O campo do filtro não pode ser vazio.",
                type = ErrorType.VALIDATION_ERROR,
                errorCode = "FILTER_FIELD_REQUIRED",
                httpStatus = HttpStatusCode.UnprocessableEntity
            )
        }
        this.filters.add(Filter(field, operator, value))
        return this
    }

    override fun addSorting(field: String, direction: SortDirection): ReportBuilder {
        if (field.isBlank()) {
            throw ReportBuilderException(
                "O campo de ordenação não pode ser vazio.",
                type = ErrorType.VALIDATION_ERROR,
                errorCode = "SORT_FIELD_REQUIRED",
                httpStatus = HttpStatusCode.UnprocessableEntity
            )
        }
        this.sorting.add(SortColumn(field, direction))
        return this
    }

    override fun setIncludeHeader(include: Boolean): ReportBuilder {
        this.includeHeader = include
        return this
    }

    override fun setIncludeFooter(include: Boolean): ReportBuilder {
        this.includeFooter = include
        return this
    }

    override fun setItemsPerPage(items: Int): ReportBuilder {
        if (items <= 0 || items > 1000) {
            throw ReportBuilderException(
                "O número de itens por página deve estar entre 1 e 1000.",
                type = ErrorType.VALIDATION_ERROR,
                errorCode = "INVALID_PAGINATION",
                httpStatus = HttpStatusCode.UnprocessableEntity
            )
        }
        this.itemsPerPage = items
        return this
    }

    /**
     * O método build é onde as VALIDAÇÕES CRUZADAS (Cross-field) acontecem.
     */
    override fun build(): ReportConfiguration {
        // 1. Validação de estado obrigatório
        if (title.isBlank()) {
            throw ReportBuilderException(
                "Configuração incompleta: título é obrigatório.",
                type = ErrorType.INVALID_CONFIGURATION,
                errorCode = "INCOMPLETE_CONFIGURATION",
                httpStatus = HttpStatusCode.BadRequest
            )
        }

        // 2. Validação Cruzada: Regra de Performance para CSV
        if (format == OutputFormat.CSV && itemsPerPage > 100) {
            throw ReportBuilderException(
                "Para exportações em CSV, o limite máximo permitido é de 100 itens por página por questões de performance.",
                type = ErrorType.VALIDATION_ERROR,
                errorCode = "CSV_PERFORMANCE_LIMIT",
                httpStatus = HttpStatusCode.UnprocessableEntity
            )
        }

        // 3. Validação Cruzada: Relatórios HTML devem ter cabeçalho
        if (format == OutputFormat.HTML && !includeHeader) {
            throw ReportBuilderException(
                "Relatórios em formato HTML exigem a inclusão do cabeçalho para renderização correta.",
                type = ErrorType.VALIDATION_ERROR,
                errorCode = "HTML_HEADER_REQUIRED",
                httpStatus = HttpStatusCode.UnprocessableEntity
            )
        }
        
        return ReportConfiguration(
            title = title,
            outputFormat = format,
            filters = filters.toList(),
            sorting = sorting.toList(),
            includeHeader = includeHeader,
            includeFooter = includeFooter,
            itemsPerPage = itemsPerPage
        )
    }
}

/**
 * Kotlin DSL: Ponto de entrada oficial para criar relatórios.
 */
fun report(block: ReportBuilder.() -> Unit): ReportConfiguration {
    return DefaultReportBuilder().apply(block).build()
}

/**
 * Factory method para quem prefere a sintaxe tradicional de builder.
 */
fun createReportBuilder(): ReportBuilder = DefaultReportBuilder()
