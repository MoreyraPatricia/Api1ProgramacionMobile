
package com.example.api1programacionmobile.presentation.viewmodel

import com.example.api1programacionmobile.domain.Reminder
import com.example.api1programacionmobile.domain.ReminderCategory
import com.example.api1programacionmobile.domain.ReminderPriority

sealed class ReminderListState {

    object Loading : ReminderListState()

    object Empty : ReminderListState()

    data class Success(
        val reminders: List<Reminder>
    ) : ReminderListState()

    data class Error(
        val message: String
    ) : ReminderListState()
}

data class ReminderUiState(
    val listState: ReminderListState = ReminderListState.Loading,
    val reminders: List<Reminder> = emptyList(),

    // Datos del formulario
    val title: String = "",
    val description: String = "",
    val selectedCategory: ReminderCategory? = null,
    val selectedPriority: ReminderPriority? = null,

    // Validaciones
    val titleError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val priorityError: String? = null,

    // Estado del guardado
    val isSaving: Boolean = false,
    val saveErrorMessage: String? = null,
    val successMessage: String? = null
)
