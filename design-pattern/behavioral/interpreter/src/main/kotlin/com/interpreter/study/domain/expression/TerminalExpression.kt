package com.interpreter.study.domain.expression

import com.interpreter.study.domain.Context

class TerminalExpression(private val literal: String) : Expression {
    override fun interpret(context: Context): Boolean {
        return context.getOrDefault(literal, false)
    }
}