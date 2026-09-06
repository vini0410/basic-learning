package com.study.TemplateMethodApp.service

import com.study.TemplateMethodApp.domain.importer.CsvDataImporter
import com.study.TemplateMethodApp.domain.importer.ImportResult
import com.study.TemplateMethodApp.domain.importer.JsonDataImporter
import com.study.TemplateMethodApp.domain.importer.XmlDataImporter
import org.springframework.stereotype.Service

@Service
class DataImportService(
    private val csvImporter: CsvDataImporter,
    private val jsonImporter: JsonDataImporter,
    private val xmlImporter: XmlDataImporter
) {

    fun importCsv(content: String): ImportResult {
        return csvImporter.processImport(content)
    }

    fun importJson(content: String): ImportResult {
        return jsonImporter.processImport(content)
    }

    fun importXml(content: String): ImportResult {
        return xmlImporter.processImport(content)
    }
}
