
package com.example.api1programacionmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api1programacionmobile.ui.theme.Api1ProgramacionMobileTheme
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// Estados posibles de un recordatorio
sealed class ReminderStatus(val label: String) {
    object Pending : ReminderStatus("Pendiente")
    object Completed : ReminderStatus("Completado")
}

// Prioridades posibles
sealed class ReminderPriority(val label: String) {
    object Low : ReminderPriority("Baja")
    object Medium : ReminderPriority("Media")
    object High : ReminderPriority("Alta")
}

// Categorías de los recordatorios
enum class ReminderCategory(val label: String) {
    STUDY("Estudio"),
    PERSONAL("Personal"),
    WORK("Trabajo")
}

// Modelo de datos inmutable
data class Reminder(
    val id: Int,
    val title: String,
    val category: ReminderCategory,
    val priority: ReminderPriority,
    val status: ReminderStatus
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Api1ProgramacionMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReminderScreen()
                }
            }
        }
    }
}

@Composable
fun ReminderScreen() {
    // Lista de datos simulados
    val reminders = remember {
        listOf(
            Reminder(
                1, "Leer el material de la actividad",
                ReminderCategory.STUDY,
                ReminderPriority.High,
                ReminderStatus.Pending
            ),
            Reminder(
                2, "Entregar el trabajo práctico",
                ReminderCategory.STUDY,
                ReminderPriority.High,
                ReminderStatus.Pending
            ),
            Reminder(
                3, "Comprar materiales",
                ReminderCategory.PERSONAL,
                ReminderPriority.Medium,
                ReminderStatus.Completed
            ),
            Reminder(
                4, "Organizar la semana",
                ReminderCategory.WORK,
                ReminderPriority.Low,
                ReminderStatus.Pending
            ),
            Reminder(
                5, "Repasar los contenidos",
                ReminderCategory.STUDY,
                ReminderPriority.Medium,
                ReminderStatus.Completed
            )
        )
    }

    // Set: categorías sin duplicados
    val categories = remember {
        reminders.map { it.category }.toSet()
    }

    // Map: cada categoría tiene un color asociado
    val categoryColors = mapOf(
        ReminderCategory.STUDY to Color(0xFF5267A8),
        ReminderCategory.PERSONAL to Color(0xFF268578),
        ReminderCategory.WORK to Color(0xFF9A62A8)
    )

    // Ejemplo de filtrado por estado
    val pendingReminders = reminders.filter {
        it.status == ReminderStatus.Pending
    }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var selectedPriority by remember { mutableStateOf("Todas") }

    val filteredReminders = reminders.filter { reminder ->
        val matchesStatus = when (selectedStatus) {
            "Pendientes" -> reminder.status == ReminderStatus.Pending
            "Completados" -> reminder.status == ReminderStatus.Completed
            else -> true
        }

        val matchesPriority = when (selectedPriority) {
            "Alta" -> reminder.priority == ReminderPriority.High
            "Media" -> reminder.priority == ReminderPriority.Medium
            "Baja" -> reminder.priority == ReminderPriority.Low
            else -> true
        }

        matchesStatus && matchesPriority
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Mis recordatorios",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Organizá tus tareas del día",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Todos: ${reminders.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Pendientes: ${pendingReminders.size}",
                color = Color(0xFF5267A8),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Categorías: ${categories.joinToString { it.label }}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Filtrar por estado", style = MaterialTheme.typography.titleSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Pendientes", "Completados").forEach { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = status },
                    label = { Text(status) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Filtrar por prioridad", style = MaterialTheme.typography.titleSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todas", "Alta", "Media", "Baja").forEach { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = { selectedPriority = priority },
                    label = { Text(priority) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredReminders) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    categoryColor = categoryColors[reminder.category]
                        ?: Color.Gray
                )
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    categoryColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reminder.category.label,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(
                            categoryColor,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )

                Text(
                    text = "Prioridad: ${reminder.priority.label}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estado: ${reminder.status.label}",
                color = if (reminder.status == ReminderStatus.Completed) {
                    Color(0xFF268578)
                } else {
                    Color(0xFFB7791F)
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
