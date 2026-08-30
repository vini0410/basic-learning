package br.com.designpattern.command.domain

data class CommandResult(
    val success: Boolean,
    val message: String,
    val taskId: Long? = null
)
