package br.com.designpattern.command.controller

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.TaskRepository
import br.com.designpattern.command.domain.command.CreateTaskCommand
import br.com.designpattern.command.domain.command.UpdateTaskStatusCommand
import br.com.designpattern.command.domain.command.DeleteTaskCommand
import br.com.designpattern.command.service.CommandProcessor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val commandProcessor: CommandProcessor,
    private val taskRepository: TaskRepository
) {

    @PostMapping
    fun createTask(@RequestBody task: Task): CommandResult {
        val command = CreateTaskCommand(task, taskRepository)
        return commandProcessor.execute(command)
    }

    @PutMapping("/{taskId}/status")
    fun updateTaskStatus(
        @PathVariable taskId: Long,
        @RequestParam status: String
    ): CommandResult {
        val command = UpdateTaskStatusCommand(taskId, status, taskRepository)
        return commandProcessor.execute(command)
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(@PathVariable taskId: Long): CommandResult {
        val command = DeleteTaskCommand(taskId, taskRepository)
        return commandProcessor.execute(command)
    }

    @PostMapping("/undo")
    fun undoLastCommand(): CommandResult {
        return commandProcessor.undoLast()
    }

    @GetMapping("/history")
    fun getCommandHistorySize(): Map<String, Int> {
        return mapOf("commandCount" to commandProcessor.getCommandHistorySize())
    }
}
