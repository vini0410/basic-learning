package com.interpreter.study.domain.expression

import com.interpreter.study.domain.Context
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class ExpressionTest {
    @Test
    fun `test TerminalExpression with greater than operator`() {
        val context = Context().apply {
            put("age > 18", true)
            put("age < 18", false)
            put("age == 25", true)
        }

        val expression = TerminalExpression("age > 18")
        assertTrue(expression.interpret(context))
    }

    @Test
    fun `test TerminalExpression with less than operator`() {
        val context = Context().apply {
            put("age < 18", false)
            put("age > 18", true)
            put("age == 25", true)
        }

        val expression = TerminalExpression("age < 18")
        assertFalse(expression.interpret(context))
    }

    @Test
    fun `test TerminalExpression with equals operator`() {
        val context = Context().apply {
            put("age == 25", true)
            put("age > 18", true)
            put("age < 18", false)
        }

        val expression = TerminalExpression("age == 25")
        assertTrue(expression.interpret(context))
    }

    @Test
    fun `test AndExpression combining expressions`() {
        val context = Context().apply {
            put("age > 18", true)
            put("name == John", true)
            put("age < 18", false)
        }

        val ageExpression = TerminalExpression("age > 18")
        val nameExpression = TerminalExpression("name == John")
        val andExpression = AndExpression(ageExpression, nameExpression)
        assertTrue(andExpression.interpret(context))
    }

    @Test
    fun `test AndExpression with false condition`() {
        val context = Context().apply {
            put("age > 18", true)
            put("name == John", false)
            put("age < 18", false)
        }

        val ageExpression = TerminalExpression("age > 18")
        val nameExpression = TerminalExpression("name == John")
        val andExpression = AndExpression(ageExpression, nameExpression)
        assertFalse(andExpression.interpret(context))
    }

    @Test
    fun `test OrExpression combining expressions`() {
        val context = Context().apply {
            put("age > 18", true)
            put("name == John", false)
            put("age < 18", false)
        }

        val ageExpression = TerminalExpression("age > 18")
        val nameExpression = TerminalExpression("name == John")
        val orExpression = OrExpression(ageExpression, nameExpression)
        assertTrue(orExpression.interpret(context))
    }

    @Test
    fun `test OrExpression with false conditions`() {
        val context = Context().apply {
            put("age > 18", false)
            put("name == John", false)
            put("age < 18", true)
        }

        val ageExpression = TerminalExpression("age > 18")
        val nameExpression = TerminalExpression("name == John")
        val orExpression = OrExpression(ageExpression, nameExpression)
        assertFalse(orExpression.interpret(context))
    }
}
