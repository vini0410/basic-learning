package br.com.designpattern.command.domain.command

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.TaskRepository

class CreateTaskCommand(
    private val task: Task,
    private val taskRepository: TaskRepository
) : Command {
    override fun execute(): CommandResult {
        return try {
            val savedTask = taskRepository.save(task)
            CommandResult(
                success = true,
                message = "Task created successfully",
                taskId = savedTask.id
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                message = "Failed to create task: ${e.message}"
            )
        }
    }

    override fun undo(): CommandResult {
        return try {
            taskRepository.deleteById(task.id!!)
            CommandResult(
                success = true,
                message = "Task creation undone successfully"
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                message = "Failed to undo task creation: ${e.message}"
            )
        }
    }
}
