package com.bcponline.dailyoffice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.components.DatePickerDialogModal
import com.bcponline.dailyoffice.model.LiturgicalDay
import com.bcponline.dailyoffice.offices.*
import kotlinx.datetime.*

@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DailyOfficeScreen()
        }
    }
}

enum class Office(val title: String) {
    MATINS("Matins"),
    VESPERS("Vespers"),
    COMPLINE("Compline")
}

enum class Version(val title: String) {
    FULL("Full"),
    CONDENSED("Condensed")
}

@Composable
fun DailyOfficeScreen() {
    val timeZone = TimeZone.currentSystemDefault()
    val now = kotlin.time.Clock.System.now().toLocalDateTime(timeZone)
    val currentDateInitially = now.date

    val initialOffice = when (now.hour) {
        in 2..11 -> Office.MATINS
        in 12..19 -> Office.VESPERS
        else -> Office.COMPLINE
    }

    var currentDate by remember { mutableStateOf(currentDateInitially) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedOffice by remember { mutableStateOf(initialOffice) }
    var selectedVersion by remember { mutableStateOf(Version.FULL) }
    var currentDay by remember {
        mutableStateOf(
            LiturgicalDay(
                com.bcponline.dailyoffice.model.Office.THANKSGIVING_MORNING,
                com.bcponline.dailyoffice.model.Office.THANKSGIVING_EVENING
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top row: DatePicker and Offices
        TabRow(selectedTabIndex = selectedOffice.ordinal + 1) {
            Tab(
                selected = false,
                onClick = { showDatePicker = true },
                text = { Text(currentDate.toString()) }
            )
            Office.entries.forEach { office ->
                Tab(
                    selected = selectedOffice == office,
                    onClick = { selectedOffice = office },
                    text = { Text(office.title) }
                )
            }
        }

        // Second row: Versions
        TabRow(selectedTabIndex = selectedVersion.ordinal) {
            Version.entries.forEach { version ->
                Tab(
                    selected = selectedVersion == version,
                    onClick = { selectedVersion = version },
                    text = { Text(version.title) }
                )
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedOffice) {
                Office.MATINS -> when (selectedVersion) {
                    Version.FULL -> MatinsFull(currentDay)
                    Version.CONDENSED -> MatinsCondensed(currentDay)
                }

                Office.VESPERS -> when (selectedVersion) {
                    Version.FULL -> VespersFull(currentDay)
                    Version.CONDENSED -> VespersCondensed(currentDay)
                }

                Office.COMPLINE -> when (selectedVersion) {
                    Version.FULL -> ComplineFull(currentDay)
                    Version.CONDENSED -> ComplineCondensed(currentDay)
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialogModal(
                onDismiss = { showDatePicker = false },
                onDateSelected = { date ->
                    currentDate = date
                    showDatePicker = false
                }
            )
        }
    }
}
