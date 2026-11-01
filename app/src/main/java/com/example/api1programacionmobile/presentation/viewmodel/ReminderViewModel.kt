
package com.example.api1programacionmobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api1programacionmobile.data.ReminderRepository
import com.example.api1programacionmobile.domain.Reminder
import com.example.api1programacionmobile.domain.ReminderCategory
import com.example.api1programacionmobile.domain.ReminderPriority
import com.example.api1programacionmobile.domain.ReminderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())

    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    // Cargar recordatorios
    fun loadReminders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                listState = ReminderListState.Loading
            )

            try {
                val reminders = repository.getReminders()

                _uiState.value = _uiState.value.copy(
                    reminders = reminders,
                    listState = if (reminders.isEmpty()) {
                        ReminderListState.Empty
                    } else {
                        ReminderListState.Success(reminders)
                    }
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    listState = ReminderListState.Error(
                        "No se pudieron cargar los recordatorios."
                    )
                )
            }
        }
    }

    // Actualizar campos del formulario
    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(
            title = value,
            titleError = null
        )
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(
            description = value.take(200),
            descriptionError = null
        )
    }

    fun updateCategory(value: ReminderCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = value,
            categoryError = null
        )
    }

    fun updatePriority(value: ReminderPriority) {
        _uiState.value = _uiState.value.copy(
            selectedPriority = value,
            priorityError = null
        )
    }

    // Validar formulario
    fun validateForm(): Boolean {
        val state = _uiState.value

        val titleError = if (state.title.trim().length < 4) {
            "El título debe tener al menos 4 caracteres."
        } else {
            null
        }

        val descriptionError = if (state.description.length > 200) {
            "La descripción no puede superar los 200 caracteres."
        } else {
            null
        }

        val categoryError = if (state.selectedCategory == null) {
            "Seleccioná una categoría."
        } else {
            null
        }

        val priorityError = if (state.selectedPriority == null) {
            "Seleccioná una prioridad."
        } else {
            null
        }

        _uiState.value = state.copy(
            titleError = titleError,
            descriptionError = descriptionError,
            categoryError = categoryError,
            priorityError = priorityError
        )

        return titleError == null &&
                descriptionError == null &&
                categoryError == null &&
                priorityError == null
    }

    // Guardar recordatorio
    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                saveErrorMessage = null,
                successMessage = null
            )

            try {
                repository.saveReminder(reminder)

                val reminders = repository.getReminders()

                _uiState.value = _uiState.value.copy(
                    reminders = reminders,
                    listState = if (reminders.isEmpty()) {
                        ReminderListState.Empty
                    } else {
                        ReminderListState.Success(reminders)
                    },
                    isSaving = false,
                    successMessage = "¡Recordatorio guardado correctamente!"
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveErrorMessage = "No se pudo guardar el recordatorio."
                )
            }
        }
    }

    // Limpiar formulario
    fun clearForm() {
        _uiState.value = _uiState.value.copy(
            title = "",
            description = "",
            selectedCategory = null,
            selectedPriority = null,
            titleError = null,
            descriptionError = null,
            categoryError = null,
            priorityError = null,
            saveErrorMessage = null
        )
    }
}
