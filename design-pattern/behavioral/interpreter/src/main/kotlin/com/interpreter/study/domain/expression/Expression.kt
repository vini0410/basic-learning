package com.interpreter.study.domain.expression

import com.interpreter.study.domain.Context

interface Expression {
    fun interpret(context: Context): Boolean
}