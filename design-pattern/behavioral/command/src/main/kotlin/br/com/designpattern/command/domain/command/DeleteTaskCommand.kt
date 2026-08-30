package br.com.designpattern.command.domain.command

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.TaskRepository

class DeleteTaskCommand(
    private val taskId: Long,
    private val taskRepository: TaskRepository
) : Command {
    override fun execute(): CommandResult {
        return try {
            taskRepository.deleteById(taskId)
            CommandResult(
                success = true,
                message = "Task deleted successfully",
                taskId = taskId
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                message = "Failed to delete task: ${e.message}"
            )
        }
    }

    override fun undo(): CommandResult {
        return try {
            // For undo, we need to restore the deleted task
            // In a real scenario, we'd have backed up the task before deletion
            CommandResult(
                success = false,
                message = "Cannot undo delete operation - task not restored"
            )
        } catch (e: Exception) {
            CommandResult(
                success = false,
                message = "Failed to undo task deletion: ${e.message}"
            )
        }
    }
}
