package com.interpreter.study.service

import com.interpreter.study.domain.Context
import com.interpreter.study.domain.expression.Expression
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class FilterParserTest {
    private val filterParser = FilterParser()

    @Test
    fun `test parsing simple string`() {
        val context = Context().apply {
            put("age > 18", true)
        }

        val expression: Expression = filterParser.parse("age > 18", context)
        assertTrue(expression.interpret(context))
    }

    @Test
    fun `test parsing complex string with AND`() {
        val context = Context().apply {
            put("age > 18", true)
            put("name == John", true)
        }

        val expression: Expression = filterParser.parse("age > 18 and name == John", context)
        assertTrue(expression.interpret(context))
    }

    @Test
    fun `test parsing complex string with OR`() {
        val context = Context().apply {
            put("age > 18", true)
            put("name == John", false)
        }

        val expression: Expression = filterParser.parse("age > 18 or name == John", context)
        assertTrue(expression.interpret(context))
    }

    @Test
    fun `test parsing complex string with AND and OR`() {
        val context = Context().apply {
            put("age > 18", true)
            put("name == John", false)
            put("status == active", true)
        }

        val expression: Expression = filterParser.parse("age > 18 and name == John or status == active", context)
        assertTrue(expression.interpret(context))
    }

    @Test
    fun `test parsing non-existent condition`() {
        val context = Context().apply {
            put("age > 18", true)
        }

        val expression: Expression = filterParser.parse("name == John", context)
        assertFalse(expression.interpret(context))
    }
}
