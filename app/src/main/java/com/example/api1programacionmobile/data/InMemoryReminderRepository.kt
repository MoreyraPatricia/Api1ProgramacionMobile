
package com.example.api1programacionmobile.data

import com.example.api1programacionmobile.domain.Reminder
import com.example.api1programacionmobile.domain.ReminderCategory
import com.example.api1programacionmobile.domain.ReminderPriority
import com.example.api1programacionmobile.domain.ReminderStatus

class InMemoryReminderRepository : ReminderRepository {

    private val reminders = mutableListOf(
        Reminder(
            id = 1,
            title = "Leer material",
            category = ReminderCategory.STUDY,
            priority = ReminderPriority.Medium,
            status = ReminderStatus.Pending
        ),
        Reminder(
            id = 2,
            title = "Entregar TP",
            category = ReminderCategory.STUDY,
            priority = ReminderPriority.High,
            status = ReminderStatus.Pending
        ),
        Reminder(
            id = 3,
            title = "Comprar materiales",
            category = ReminderCategory.PERSONAL,
            priority = ReminderPriority.Medium,
            status = ReminderStatus.Pending
        ),
        Reminder(
            id = 4,
            title = "Organizar semana",
            category = ReminderCategory.PERSONAL,
            priority = ReminderPriority.Low,
            status = ReminderStatus.Completed
        ),
        Reminder(
            id = 5,
            title = "Repasar contenidos",
            category = ReminderCategory.STUDY,
            priority = ReminderPriority.High,
            status = ReminderStatus.Pending
        )
    )

    override suspend fun getReminders(): List<Reminder> {
        return reminders.toList()
    }

    override suspend fun saveReminder(reminder: Reminder): Reminder {
        kotlinx.coroutines.delay(1500)

        if (reminder.title.equals("ERROR", ignoreCase = true)) {
            throw Exception("Error simulado al guardar.")
        }

        val index = reminders.indexOfFirst { it.id == reminder.id }

        if (index >= 0) {
            reminders[index] = reminder
        } else {
            reminders.add(reminder)
        }

        return reminder
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        return reminders.find { it.id == id }
    }
}
