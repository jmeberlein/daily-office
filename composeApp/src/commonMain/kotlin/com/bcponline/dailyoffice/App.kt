package com.bcponline.dailyoffice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.LiturgicalDay
import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.data.FileRegistry
import com.bcponline.dailyoffice.data.ProperFetcher
import com.bcponline.dailyoffice.data.ProperParser
import com.bcponline.dailyoffice.ui.Compline
import com.bcponline.dailyoffice.ui.Matins
import com.bcponline.dailyoffice.ui.Vespers
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock

private val THANKSGIVING_DAY = LiturgicalDay(
    morning = Office.THANKSGIVING_MORNING,
    evening = Office.THANKSGIVING_EVENING
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    var selectedDate by remember { mutableStateOf(today) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var condensed by remember { mutableStateOf(false) }
    var forceTwoReadings by remember { mutableStateOf(false) }
    var useOptionalSaints by remember { mutableStateOf(false) }
    var useExtraFeasts by remember { mutableStateOf(false) }
    var liturgicalDay by remember { mutableStateOf<LiturgicalDay>(THANKSGIVING_DAY) }
    var selectedService by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedDate, forceTwoReadings, useOptionalSaints, useExtraFeasts) {
        scope.launch {
            FileRegistry.loadFiles("daily_propers")
            if (useOptionalSaints) FileRegistry.loadFiles("optional_feasts")
            if (useExtraFeasts) FileRegistry.loadFiles("extra_feasts")
            ProperParser.loadFilesForDate(selectedDate)
            liturgicalDay = ProperFetcher.getProperForDate(selectedDate, forceTwoReadings, useOptionalSaints, useExtraFeasts)
        }
    }

    val currentOfficeColor = when (selectedService) {
        0 -> liturgicalDay.morning.color
        else -> liturgicalDay.evening.color
    }
    val bg = if (currentOfficeColor == LiturgicalColor.NONE) Color(0xFFFFFBFE) else currentOfficeColor.background
    val colorScheme = lightColorScheme(
        background = bg,
        surface = bg,
        surfaceVariant = bg,
        surfaceContainer = bg,
        surfaceContainerHigh = bg,
        surfaceContainerHighest = bg,
        surfaceContainerLow = bg,
        surfaceContainerLowest = bg,
        primaryContainer = bg,
    )

    val services = listOf("Matins", "Vespers", "Compline")

    MaterialTheme(colorScheme = colorScheme) {
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC).date
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
            ) { DatePicker(state = datePickerState) }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(selectedDate.toString(), style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Text("⋮", style = MaterialTheme.typography.titleLarge)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Condensed") },
                                trailingIcon = { Switch(checked = condensed, onCheckedChange = { condensed = it }) },
                                onClick = { condensed = !condensed }
                            )
                            DropdownMenuItem(
                                text = { Text("Two Readings") },
                                trailingIcon = { Switch(checked = forceTwoReadings, onCheckedChange = { forceTwoReadings = it }) },
                                onClick = { forceTwoReadings = !forceTwoReadings }
                            )
                            DropdownMenuItem(
                                text = { Text("Optional Saints") },
                                trailingIcon = { Switch(checked = useOptionalSaints, onCheckedChange = { useOptionalSaints = it; if (!it) useExtraFeasts = false }) },
                                onClick = { useOptionalSaints = !useOptionalSaints; if (!useOptionalSaints) useExtraFeasts = false }
                            )
                            DropdownMenuItem(
                                enabled = useOptionalSaints,
                                text = { Text("Even More Saints") },
                                trailingIcon = { Switch(enabled = useOptionalSaints, checked = useExtraFeasts, onCheckedChange = { useExtraFeasts = it }) },
                                onClick = { if (useOptionalSaints) useExtraFeasts = !useExtraFeasts }
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                TabRow(selectedTabIndex = selectedService) {
                    services.forEachIndexed { index, name ->
                        Tab(
                            selected = selectedService == index,
                            onClick = { selectedService = index },
                            text = { Text(name) }
                        )
                    }
                }
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    when (selectedService) {
                        0 -> Matins(selectedDate, liturgicalDay, condensed)
                        1 -> Vespers(selectedDate, liturgicalDay, condensed)
                        2 -> Compline(selectedDate, liturgicalDay, condensed)
                    }
                }
            }
        }
    }
}