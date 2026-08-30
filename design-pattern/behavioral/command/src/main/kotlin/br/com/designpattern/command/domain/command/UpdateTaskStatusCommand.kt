package br.com.designpattern.command.domain.command

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.TaskRepository
import br.com.designpattern.command.domain.TaskStatus

class UpdateTaskStatusCommand(
    private val taskId: Long,
    private val newStatus: String,
    private val taskRepository: TaskRepository
) : Command {
    override fun execute(): CommandResult {
        return try {
            val existingTask = taskRepository.findById(taskId)
                .orElseThrow { RuntimeException("Task not found") }
            
            val updatedTask = existingTask.copy(status = TaskStatus.valueOf(newStatus))
            taskRepository.save(updatedTask)
            
            CommandResult(
                success = true,
                message = "Task status updated to $newStatus",
                taskId = taskId
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                message = "Failed to update task status: ${e.message}"
            )
        }
    }

    override fun undo(): CommandResult {
        return try {
            val existingTask = taskRepository.findById(taskId)
                .orElseThrow { RuntimeException("Task not found") }
            
            // Revert to previous status (PENDING in this case)
            val revertedTask = existingTask.copy(status = TaskStatus.PENDING)
            taskRepository.save(revertedTask)
            
            CommandResult(
                success = true,
                message = "Task status reverted to PENDING",
                taskId = taskId
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                message = "Failed to undo task status update: ${e.message}"
            )
        }
    }
}
