package com.bcponline.dailyoffice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.bcponline.dailyoffice.ui.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

private enum class NavDest(val label: String) {
    OVERVIEW("Overview"),
    MATINS("Morning Prayer"),
    VESPERS("Evening Prayer"),
    SETTINGS("Settings"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var currentDest by remember { mutableStateOf(NavDest.OVERVIEW) }
    var showDatePicker by remember { mutableStateOf(false) }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentDest.label) },
                    actions = {
                        TextButton(onClick = { showDatePicker = true }) { Text("Date") }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavDest.entries.forEach { dest ->
                        NavigationBarItem(
                            selected = currentDest == dest,
                            onClick = { currentDest = dest },
                            icon = {},
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (currentDest) {
                    NavDest.OVERVIEW -> OverviewScreen()
                    NavDest.MATINS   -> MatinsScreen()
                    NavDest.VESPERS  -> VespersScreen()
                    NavDest.SETTINGS -> SettingsScreen()
                }
            }
        }

        if (showDatePicker) {
            val state = rememberDatePickerState(
                initialSelectedDateMillis = SelectedDate.date.value.let { d ->
                    kotlinx.datetime.LocalDateTime(d.year, d.month, d.dayOfMonth, 12, 0)
                        .toInstant(TimeZone.UTC).toEpochMilliseconds()
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        state.selectedDateMillis?.let { millis ->
                            SelectedDate.date.value = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC).date
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state)
            }
        }
    }
}
