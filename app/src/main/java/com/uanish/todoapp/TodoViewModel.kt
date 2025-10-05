package com.uanish.todoapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class TodoViewModel : ViewModel() {
    private val _items = mutableStateListOf<TodoItem>()
    val items: List<TodoItem> get() = _items

    fun add(label: String) {
        val trimmed = label.trim()
        if (trimmed.isNotEmpty()) {
            _items.add(TodoItem(label = trimmed))
        }
    }

    fun toggle(id: String, checked: Boolean) {
        val index = _items.indexOfFirst { it.id == id }
        if (index != -1) {
            _items[index] = _items[index].copy(isDone = checked)
        }
    }

    fun delete(id: String) {
        _items.removeAll { it.id == id }
    }
}
