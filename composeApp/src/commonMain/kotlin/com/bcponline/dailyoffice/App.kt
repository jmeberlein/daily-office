package com.bcponline.dailyoffice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.LiturgicalDay
import com.bcponline.dailyoffice.data.FileRegistry
import com.bcponline.dailyoffice.data.ProperFetcher
import com.bcponline.dailyoffice.data.ProperParser
import com.bcponline.dailyoffice.ui.Compline
import com.bcponline.dailyoffice.ui.Matins
import com.bcponline.dailyoffice.ui.Vespers
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.date
    val initialDate = if (now.hour < 4) today.minus(1, kotlinx.datetime.DateTimeUnit.DAY) else today
    val initialService = if (now.hour < 4 || now.hour >= 16) 1 else 0

    var selectedDate by remember { mutableStateOf(initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var condensed by remember { mutableStateOf(false) }
    var forceTwoReadings by remember { mutableStateOf(Settings.forceTwoReadings) }
    var useOptionalSaints by remember { mutableStateOf(Settings.useOptionalSaints) }
    var useExtraFeasts by remember { mutableStateOf(Settings.useExtraFeasts) }
    var liturgicalDay by remember { mutableStateOf<LiturgicalDay?>(null) }
    var selectedService by remember { mutableStateOf(initialService) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedDate, forceTwoReadings, useOptionalSaints, useExtraFeasts) {
        liturgicalDay = null
        scope.launch {
            FileRegistry.loadFiles("daily_propers")
            if (useOptionalSaints) FileRegistry.loadFiles("optional_feasts")
            if (useExtraFeasts) FileRegistry.loadFiles("extra_feasts")
            ProperParser.loadFilesForDate(selectedDate)
            liturgicalDay = ProperFetcher.getProperForDate(selectedDate, forceTwoReadings, useOptionalSaints, useExtraFeasts)
        }
    }

    val currentOfficeColor = when (selectedService) {
        0 -> liturgicalDay?.morning?.color
        else -> liturgicalDay?.evening?.color
    } ?: LiturgicalColor.NONE
    val bg = if (currentOfficeColor == LiturgicalColor.NONE) Color(0xFFFFFBFE) else currentOfficeColor.background
    val fg = if (currentOfficeColor == LiturgicalColor.NONE) Color(0xFF1C1B1F) else currentOfficeColor.onBackground
    val primary = if (currentOfficeColor == LiturgicalColor.NONE) Color(0xFF1C1B1F) else currentOfficeColor.primary
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
        onBackground = fg,
        onSurface = fg,
        onSurfaceVariant = fg,
        primary = primary,
        onPrimary = bg,
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
                                trailingIcon = { Switch(checked = forceTwoReadings, onCheckedChange = { forceTwoReadings = it; Settings.forceTwoReadings = it }) },
                                onClick = { forceTwoReadings = !forceTwoReadings; Settings.forceTwoReadings = forceTwoReadings }
                            )
                            DropdownMenuItem(
                                text = { Text("Optional Saints") },
                                trailingIcon = { Switch(checked = useOptionalSaints, onCheckedChange = { useOptionalSaints = it; Settings.useOptionalSaints = it; if (!it) { useExtraFeasts = false; Settings.useExtraFeasts = false } }) },
                                onClick = { useOptionalSaints = !useOptionalSaints; Settings.useOptionalSaints = useOptionalSaints; if (!useOptionalSaints) { useExtraFeasts = false; Settings.useExtraFeasts = false } }
                            )
                            DropdownMenuItem(
                                enabled = useOptionalSaints,
                                text = { Text("Even More Saints") },
                                trailingIcon = { Switch(enabled = useOptionalSaints, checked = useExtraFeasts, onCheckedChange = { useExtraFeasts = it; Settings.useExtraFeasts = it }) },
                                onClick = { if (useOptionalSaints) { useExtraFeasts = !useExtraFeasts; Settings.useExtraFeasts = useExtraFeasts } }
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
                    val day = liturgicalDay
                    if (day == null) {
                        Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else when (selectedService) {
                        0 -> Matins(selectedDate, day, condensed)
                        1 -> Vespers(selectedDate, day, condensed)
                        2 -> Compline(selectedDate, day, condensed)
                    }
                }
            }
        }
    }
}