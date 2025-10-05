package com.uanish.todoapp

data class TodoItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String,
    val isDone: Boolean = false
)
