
package com.example.api1programacionmobile.domain

sealed class ReminderStatus(val label: String) {
    object Pending : ReminderStatus("Pendiente")
    object Completed : ReminderStatus("Completado")
}

sealed class ReminderPriority(val label: String) {
    object Low : ReminderPriority("Baja")
    object Medium : ReminderPriority("Media")
    object High : ReminderPriority("Alta")
}

enum class ReminderCategory(val label: String) {
    STUDY("Estudio"),
    PERSONAL("Personal"),
    WORK("Trabajo")
}

data class Reminder(
    val id: Int,
    val title: String,
    val description: String = "",
    val category: ReminderCategory,
    val priority: ReminderPriority,
    val status: ReminderStatus
)
