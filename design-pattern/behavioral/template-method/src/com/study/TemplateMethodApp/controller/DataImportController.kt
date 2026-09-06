package com.study.TemplateMethodApp.controller

import com.study.TemplateMethodApp.domain.importer.ImportResult
import com.study.TemplateMethodApp.dto.ImportPayloadRequest
import com.study.TemplateMethodApp.service.DataImportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/import")
class DataImportController(
    private val importService: DataImportService
) {

    @PostMapping("/csv")
    fun importCsv(@RequestBody request: ImportPayloadRequest): ResponseEntity<ImportResult> {
        val result = importService.importCsv(request.content)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/json")
    fun importJson(@RequestBody request: ImportPayloadRequest): ResponseEntity<ImportResult> {
        val result = importService.importJson(request.content)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/xml")
    fun importXml(@RequestBody request: ImportPayloadRequest): ResponseEntity<ImportResult> {
        val result = importService.importXml(request.content)
        return ResponseEntity.ok(result)
    }
}
