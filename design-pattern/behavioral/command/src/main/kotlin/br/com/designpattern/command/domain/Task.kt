package br.com.designpattern.command.domain

import jakarta.persistence.*

@Entity
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val description: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val scheduledTime: String
)

enum class TaskStatus {
    PENDING, 
    IN_PROGRESS, 
    COMPLETED, 
    FAILED
}
