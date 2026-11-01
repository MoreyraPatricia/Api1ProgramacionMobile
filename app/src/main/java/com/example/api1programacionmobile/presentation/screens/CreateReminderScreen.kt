
package com.example.api1programacionmobile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.api1programacionmobile.domain.ReminderCategory
import com.example.api1programacionmobile.domain.ReminderPriority

@Composable
fun CreateReminderScreen(
    title: String,
    description: String,
    selectedCategory: ReminderCategory?,
    selectedPriority: ReminderPriority?,
    titleError: String?,
    descriptionError: String?,
    categoryError: String?,
    priorityError: String?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (ReminderCategory) -> Unit,
    onPriorityChange: (ReminderPriority) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isSaving: Boolean = false,
    errorMessage: String? = null
) {
    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val formIsValid =
        title.trim().length >= 4 &&
                description.length <= 200 &&
                selectedCategory != null &&
                selectedPriority != null

    val displayedTitleError = titleError ?: if (
        title.isNotEmpty() && title.trim().length < 4
    ) {
        "El título debe tener al menos 4 caracteres."
    } else null

    val displayedDescriptionError = descriptionError ?: if (
        description.length > 200
    ) {
        "La descripción no puede superar los 200 caracteres."
    } else null

    val displayedCategoryError = categoryError ?: if (
        selectedCategory == null
    ) {
        "Seleccioná una categoría."
    } else null

    val displayedPriorityError = priorityError ?: if (
        selectedPriority == null
    ) {
        "Seleccioná una prioridad."
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nuevo recordatorio",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = displayedTitleError != null
        )

        if (displayedTitleError != null) {
            Text(
                text = displayedTitleError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            isError = displayedDescriptionError != null
        )

        Text(
            text = "${description.length}/200 caracteres",
            style = MaterialTheme.typography.bodySmall
        )

        if (displayedDescriptionError != null) {
            Text(
                text = displayedDescriptionError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(text = "Categoría")

        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = { categoryExpanded = true }) {
                Text(selectedCategory?.label ?: "Seleccionar categoría")
            }

            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                ReminderCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.label) },
                        onClick = {
                            onCategoryChange(category)
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        if (displayedCategoryError != null) {
            Text(
                text = displayedCategoryError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(text = "Prioridad")

        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = { priorityExpanded = true }) {
                Text(selectedPriority?.label ?: "Seleccionar prioridad")
            }

            DropdownMenu(
                expanded = priorityExpanded,
                onDismissRequest = { priorityExpanded = false }
            ) {
                listOf(
                    ReminderPriority.Low,
                    ReminderPriority.Medium,
                    ReminderPriority.High
                ).forEach { priority ->
                    DropdownMenuItem(
                        text = { Text(priority.label) },
                        onClick = {
                            onPriorityChange(priority)
                            priorityExpanded = false
                        }
                    )
                }
            }
        }

        if (displayedPriorityError != null) {
            Text(
                text = displayedPriorityError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving && formIsValid
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar recordatorio")
        }

        Button(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Text("Cancelar")
        }
    }
}
