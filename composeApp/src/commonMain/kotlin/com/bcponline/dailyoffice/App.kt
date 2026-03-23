package com.bcponline.dailyoffice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcponline.dailyoffice.model.Season
import com.bcponline.dailyoffice.ui.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun App() {
    MaterialTheme {
        val vm: OfficeViewModel = viewModel { OfficeViewModel() }
        val state by vm.state.collectAsStateWithLifecycle()
        val settings by vm.settings.collectAsStateWithLifecycle()

        val defaultTab = remember {
            val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
            if (hour in 4..15) 0 else 1
        }
        var selectedTab by remember { mutableIntStateOf(defaultTab) }
        var showDatePicker by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }

        val currentDate = (state as? OfficeUiState.Success)?.date
        val successState = state as? OfficeUiState.Success
        val office = when {
            successState == null -> null
            selectedTab == 0 -> successState.day.morning
            else -> successState.day.evening
        }
        // Show both names if they differ, otherwise just one
        val morningName = successState?.day?.morning?.name
        val eveningName = successState?.day?.evening?.name
        val title = when {
            morningName == null -> ""
            morningName == eveningName -> morningName
            selectedTab == 0 -> morningName
            else -> eveningName ?: morningName
        }
        val subtitle = office?.season
            ?.takeIf { it != Season.NONE }
            ?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
            ?: ""
        val primary = office?.let { officeColorsFor(it).primary }
            ?: MaterialTheme.colorScheme.primary

        if (showDatePicker && currentDate != null) {
            OfficeDatePickerDialog(
                currentDate = currentDate,
                onDismiss = { showDatePicker = false },
                onConfirm = { vm.load(it, settings); showDatePicker = false }
            )
        }
        if (showSettings) {
            OfficeSettingsDialog(
                current = settings,
                onDismiss = { showSettings = false },
                onConfirm = { vm.load(currentDate ?: return@OfficeSettingsDialog, it); showSettings = false }
            )
        }

        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            // ── Header ────────────────────────────────────────────────
            Surface(tonalElevation = 2.dp, color = office?.let { officeColorsFor(it).bg } ?: MaterialTheme.colorScheme.surface) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            if (title.isNotBlank()) {
                                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = primary)
                            }
                            if (subtitle.isNotBlank()) {
                                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = primary.copy(alpha = 0.7f))
                            }
                        }
                        if (currentDate != null) {
                            OutlinedButton(onClick = { showDatePicker = true }) {
                                Text("${currentDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentDate.dayOfMonth}, ${currentDate.year}")
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        OutlinedButton(onClick = { showSettings = true }) { Text("⚙") }
                    }
                    TabRow(selectedTabIndex = selectedTab, containerColor = androidx.compose.ui.graphics.Color.Transparent, contentColor = primary) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Matins") })
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Vespers") })
                    }
                }
            }

            if (selectedTab == 0) MatinsScreen(vm) else VespersScreen(vm)
        }
    }
}
