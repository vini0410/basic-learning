package com.interpreter.study.controller

import com.interpreter.study.domain.Context
import com.interpreter.study.domain.FilterRequest
import com.interpreter.study.service.FilterParser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/filters")
class FilterController(private val filterParser: FilterParser) {
    
    @PostMapping("/evaluate")
    fun evaluateFilter(@RequestBody request: Map<String, Any>): Boolean {
        val filter = request["filter"] as? String ?: ""
        val contextMap = (request["context"] as? Map<String, Boolean>) ?: emptyMap()
        val context = Context().apply { putAll(contextMap) }
        
        val expression = filterParser.parse(filter, context)
        return expression.interpret(context)
    }
}