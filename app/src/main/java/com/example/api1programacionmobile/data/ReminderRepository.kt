
package com.example.api1programacionmobile.data

import com.example.api1programacionmobile.domain.Reminder

interface ReminderRepository {
    suspend fun getReminders(): List<Reminder>

    suspend fun saveReminder(reminder: Reminder): Reminder

    suspend fun getReminderById(id: Int): Reminder?
}
