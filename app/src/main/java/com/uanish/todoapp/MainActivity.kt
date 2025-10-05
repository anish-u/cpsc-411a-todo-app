package com.uanish.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { TodoApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(vm: TodoViewModel = viewModel()) {
    var input by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    // 🔑 Observe the state list directly — not filtered copies
    val items = vm.items

    Scaffold(topBar = { TopAppBar(title = { Text("TODO List") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AddRow(
                text = input,
                onTextChange = {
                    input = it
                    error = null
                },
                onSubmit = {
                    val trimmed = input.trim()
                    if (trimmed.isEmpty()) {
                        error = "Please enter a task"
                    } else {
                        vm.add(trimmed)
                        input = ""
                    }
                },
                error = error
            )

            // Derived sections (computed on each recomposition)
            val active = items.filter { !it.isDone }
            val completed = items.filter { it.isDone }

            Section(
                title = "Items",
                items = active,
                emptyText = "No items yet",
                onToggle = { id, checked -> vm.toggle(id, checked) },
                onDelete = { id -> vm.delete(id) }
            )

            Section(
                title = "Completed Items",
                items = completed,
                emptyText = "Nothing completed yet",
                onToggle = { id, checked -> vm.toggle(id, checked) },
                onDelete = { id -> vm.delete(id) }
            )
        }
    }
}

@Composable
fun AddRow(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    error: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter the task name") },
                singleLine = true,
                isError = error != null
            )
            Button(
                onClick = onSubmit,
                enabled = text.trim().isNotEmpty()
            ) { Text("Add") }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun Section(
    title: String,
    items: List<TodoItem>,
    emptyText: String,
    onToggle: (String, Boolean) -> Unit,
    onDelete: (String) -> Unit
) {
    if (items.isEmpty()) {
        Text(emptyText, style = MaterialTheme.typography.bodyMedium)
        return
    }

    Text(title, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items, key = { it.id }) { item ->
            TodoRow(
                item = item,
                onCheckedChange = { checked -> onToggle(item.id, checked) },
                onDelete = { onDelete(item.id) }
            )
        }
    }
}

@Composable
fun TodoRow(
    item: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(checked = item.isDone, onCheckedChange = onCheckedChange)
            Text(text = item.label, modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Delete")
            }
        }
    }
}
