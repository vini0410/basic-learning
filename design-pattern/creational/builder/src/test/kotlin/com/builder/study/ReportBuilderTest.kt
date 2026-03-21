package com.builder.study

import com.builder.study.builders.createReportBuilder
import com.builder.study.builders.report
import com.builder.study.exceptions.ReportBuilderException
import com.builder.study.models.OutputFormat
import com.builder.study.models.ReportRequest
import com.builder.study.models.SortDirection
import io.ktor.http.*
import kotlin.test.*

class ReportBuilderTest {

    @Test
    fun `should build a report from request-like data`() {
        val request = ReportRequest(
            reportName = "API Generated",
            format = "excel",
            filterBy = "active",
            filterValue = "true",
            limit = 100
        )

        val config = report {
            setTitle(request.reportName)
            setOutputFormat(OutputFormat.valueOf(request.format.uppercase()))
            request.filterBy?.let { addFilter(it, "==", request.filterValue ?: "") }
            request.limit?.let { setItemsPerPage(it) }
        }

        assertNotNull(config)
        assertEquals("API Generated", config.title)
        assertEquals(OutputFormat.EXCEL, config.outputFormat)
        assertEquals(1, config.filters.size)
        assertEquals("active", config.filters[0].field)
        assertEquals(100, config.itemsPerPage)
    }

    @Test
    fun `should build a report using the traditional builder`() {
        val builder = createReportBuilder()
        val config = builder
            .setTitle("Test Report")
            .setOutputFormat(OutputFormat.CSV)
            .addFilter("category", "==", "electronics")
            .addSorting("price", SortDirection.DESC)
            .setIncludeHeader(false)
            .setItemsPerPage(25)
            .build()

        assertNotNull(config)
        assertEquals("Test Report", config.title)
        assertEquals(OutputFormat.CSV, config.outputFormat)
        assertEquals(1, config.filters.size)
        assertEquals("category", config.filters[0].field)
        assertEquals("==", config.filters[0].operator)
        assertEquals("electronics", config.filters[0].value)
        assertEquals(1, config.sorting.size)
        assertEquals("price", config.sorting[0].field)
        assertEquals(SortDirection.DESC, config.sorting[0].direction)
        assertEquals(false, config.includeHeader)
        assertEquals(25, config.itemsPerPage)
    }

    @Test
    fun `should build a report using the DSL builder`() {
        val config = report {
            setTitle("DSL Report")
            setOutputFormat(OutputFormat.JSON)
            addFilter("brand", "==", "Samsung")
            addSorting("name")
            setItemsPerPage(10)
        }

        assertNotNull(config)
        assertEquals("DSL Report", config.title)
        assertEquals(OutputFormat.JSON, config.outputFormat)
        assertEquals("brand", config.filters[0].field)
        assertEquals("Samsung", config.filters[0].value)
        assertEquals("name", config.sorting[0].field)
        assertEquals(SortDirection.ASC, config.sorting[0].direction)
        assertEquals(10, config.itemsPerPage)
    }

    @Test
    fun `should have default values when only title is specified`() {
        val config = createReportBuilder().setTitle("Default Values").build()

        assertNotNull(config)
        assertEquals("Default Values", config.title)
        assertEquals(OutputFormat.PDF, config.outputFormat)
        assertTrue(config.filters.isEmpty())
        assertTrue(config.sorting.isEmpty())
        assertEquals(true, config.includeHeader)
        assertEquals(true, config.includeFooter)
        assertEquals(50, config.itemsPerPage)
    }

    @Test
    fun `should throw exception when building without title`() {
        val exception = assertFailsWith<ReportBuilderException> {
            createReportBuilder().build()
        }
        assertEquals("Configuração incompleta: título é obrigatório.", exception.message)
        assertEquals(HttpStatusCode.BadRequest, exception.httpStatus)
    }

    @Test
    fun `should throw exception with invalid items per page`() {
        val builder = createReportBuilder()
        val exception = assertFailsWith<ReportBuilderException> {
            builder.setItemsPerPage(0)
        }
        assertEquals("O número de itens por página deve estar entre 1 e 1000.", exception.message)
        assertEquals(HttpStatusCode.UnprocessableEntity, exception.httpStatus)
    }

    @Test
    fun `should throw exception for CSV with too many items`() {
        val builder = createReportBuilder()
        val exception = assertFailsWith<ReportBuilderException> {
            builder.setTitle("CSV Heavy")
                .setOutputFormat(OutputFormat.CSV)
                .setItemsPerPage(200)
                .build()
        }
        assertEquals("CSV_PERFORMANCE_LIMIT", exception.errorCode)
        assertEquals(HttpStatusCode.UnprocessableEntity, exception.httpStatus)
    }
}
