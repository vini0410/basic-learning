package com.interpreter.study.service

import com.interpreter.study.domain.Context
import com.interpreter.study.domain.expression.*
import org.springframework.stereotype.Service

@Service
class FilterParser {
    fun parse(filter: String, context: Context): Expression {
        // First handle AND operations (higher precedence), then OR
        val andParts = filter.split(" and ").map { it.trim() }
        if (andParts.size > 1) {
            return buildAndExpression(andParts)
        }
        
        val orParts = filter.split(" or ").map { it.trim() }
        if (orParts.size > 1) {
            return buildOrExpression(orParts)
        }
        
        return TerminalExpression(filter.trim())
    }
    
    private fun buildAndExpression(parts: List<String>): Expression {
        val expressions = parts.map { parse(it, Context()) }
        var result = expressions[0]
        for (i in 1 until expressions.size) {
            result = AndExpression(result, expressions[i])
        }
        return result
    }
    
    private fun buildOrExpression(parts: List<String>): Expression {
        val expressions = parts.map { parse(it, Context()) }
        var result = expressions[0]
        for (i in 1 until expressions.size) {
            result = OrExpression(result, expressions[i])
        }
        return result
    }
}