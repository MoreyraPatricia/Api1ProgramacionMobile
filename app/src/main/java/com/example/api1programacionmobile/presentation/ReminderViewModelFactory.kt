package com.example.api1programacionmobile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.api1programacionmobile.data.InMemoryReminderRepository
import com.example.api1programacionmobile.presentation.viewmodel.ReminderViewModel

class ReminderViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            val repository = InMemoryReminderRepository()

            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}