
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.api1programacionmobile.domain.Reminder
import com.example.api1programacionmobile.domain.ReminderCategory
import com.example.api1programacionmobile.domain.ReminderPriority
import com.example.api1programacionmobile.domain.ReminderStatus
import com.example.api1programacionmobile.presentation.ReminderViewModelFactory
import com.example.api1programacionmobile.presentation.viewmodel.ReminderListState
import com.example.api1programacionmobile.presentation.viewmodel.ReminderViewModel
import com.example.api1programacionmobile.ui.theme.Api1ProgramacionMobileTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Api1ProgramacionMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val reminderViewModel: ReminderViewModel = viewModel(
                        factory = ReminderViewModelFactory()
                    )

                    NavHost(
                        navController = navController,
                        startDestination = "reminders"
                    ) {
                        composable("reminders") {
                            ReminderScreen(
                                viewModel = reminderViewModel,
                                onCreateReminder = {
                                    navController.navigate("create")
                                },
                                onOpenDetail = { reminderId ->
                                    navController.navigate("detail/$reminderId")
                                }
                            )
                        }

                        // Pantalla para crear recordatorios
                        composable("create") {
                            val uiState by reminderViewModel.uiState
                                .collectAsStateWithLifecycle()

                            var wasSaving by remember {
                                mutableStateOf(false)
                            }

                            LaunchedEffect(
                                uiState.isSaving,
                                uiState.saveErrorMessage
                            ) {
                                if (uiState.isSaving) {
                                    wasSaving = true
                                } else if (
                                    wasSaving &&
                                    uiState.saveErrorMessage == null
                                ) {
                                    wasSaving = false
                                    reminderViewModel.clearForm()
                                    navController.popBackStack()
                                }
                            }

                            com.example.api1programacionmobile.presentation.screens.CreateReminderScreen(
                                title = uiState.title,
                                description = uiState.description,
                                selectedCategory = uiState.selectedCategory,
                                selectedPriority = uiState.selectedPriority,
                                titleError = uiState.titleError,
                                descriptionError = uiState.descriptionError,
                                categoryError = uiState.categoryError,
                                priorityError = uiState.priorityError,
                                onTitleChange = reminderViewModel::updateTitle,
                                onDescriptionChange = reminderViewModel::updateDescription,
                                onCategoryChange = reminderViewModel::updateCategory,
                                onPriorityChange = reminderViewModel::updatePriority,
                                isSaving = uiState.isSaving,
                                errorMessage = uiState.saveErrorMessage,
                                onSave = {
                                    if (reminderViewModel.validateForm()) {
                                        val currentState =
                                            reminderViewModel.uiState.value

                                        val newReminder = Reminder(
                                            id = (currentState.reminders.maxOfOrNull {
                                                it.id
                                            } ?: 0) + 1,
                                            title = currentState.title.trim(),
                                            description = currentState.description.trim(),
                                            category = currentState.selectedCategory!!,
                                            priority = currentState.selectedPriority!!,
                                            status = ReminderStatus.Pending
                                        )

                                        reminderViewModel.saveReminder(newReminder)
                                    }
                                },
                                onCancel = {
                                    reminderViewModel.clearForm()
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Pantalla de detalle
                        composable("detail/{reminderId}") { backStackEntry ->
                            val uiState by reminderViewModel.uiState
                                .collectAsStateWithLifecycle()

                            val reminderId = backStackEntry.arguments
                                ?.getString("reminderId")
                                ?.toIntOrNull()

                            val reminder = uiState.reminders.find {
                                it.id == reminderId
                            }

                            if (reminder != null) {
                                com.example.api1programacionmobile.presentation.screens.ReminderDetailScreen(
                                    reminder = reminder,
                                    onBack = {
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                Text(
                                    text = "No se encontró el recordatorio."
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel,
    onCreateReminder: () -> Unit,
    onOpenDetail: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reminders = uiState.reminders

    when (val state = uiState.listState) {
        is ReminderListState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Cargando recordatorios...")
            }
            return
        }

        is ReminderListState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.loadReminders()
                    }
                ) {
                    Text("Reintentar")
                }
            }
            return
        }

        is ReminderListState.Empty,
        is ReminderListState.Success -> Unit
    }

    val categories = remember(reminders) {
        reminders.map { it.category }.toSet()
    }

    val categoryColors = mapOf(
        ReminderCategory.STUDY to Color(0xFF5267A8),
        ReminderCategory.PERSONAL to Color(0xFF268578),
        ReminderCategory.WORK to Color(0xFF9A62A8)
    )

    val pendingReminders = reminders.filter {
        it.status == ReminderStatus.Pending
    }

    var selectedStatus by remember {
        mutableStateOf("Todos")
    }

    var selectedPriority by remember {
        mutableStateOf("Todas")
    }

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

        if (uiState.successMessage != null) {
            Text(
                text = uiState.successMessage ?: "",
                color = Color(0xFF268578),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCreateReminder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("＋ Nuevo recordatorio")
        }

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

        Text(
            text = "Filtrar por estado",
            style = MaterialTheme.typography.titleSmall
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Pendientes", "Completados").forEach { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = {
                        selectedStatus = status
                    },
                    label = {
                        Text(status)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Filtrar por prioridad",
            style = MaterialTheme.typography.titleSmall
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todas", "Alta", "Media", "Baja").forEach { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = {
                        selectedPriority = priority
                    },
                    label = {
                        Text(priority)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredReminders.isEmpty()) {
            Text(
                text = if (reminders.isEmpty()) {
                    "Todavía no hay recordatorios. ¡Creá el primero!"
                } else {
                    "No hay recordatorios para mostrar."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredReminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        categoryColor = categoryColors[reminder.category]
                            ?: Color.Gray,
                        onClick = {
                            onOpenDetail(reminder.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    categoryColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
                        .padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        )
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
