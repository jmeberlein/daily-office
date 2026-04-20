package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

@Composable
fun OverviewScreen(vm: OverviewViewModel = viewModel { OverviewViewModel() }) {
    val day by vm.day.collectAsStateWithLifecycle()
    val morningTab by vm.morningCollectTab.collectAsStateWithLifecycle()
    val eveningTab by vm.eveningCollectTab.collectAsStateWithLifecycle()
    val color = day?.morning?.color ?: LiturgicalColor.NONE

    val dateString = vm.date.collectAsStateWithLifecycle().value.format(LocalDate.Format {
        monthName(MonthNames.ENGLISH_FULL); char(' '); dayOfMonth(); chars(", "); year()
    })

    MaterialTheme(
        colorScheme = if (color != LiturgicalColor.NONE)
            MaterialTheme.colorScheme.copy(
                background = color.background,
                surface = color.background,
                primary = color.primary,
                onBackground = color.onBackground,
                onSurface = color.onBackground,
                onSurfaceVariant = color.onBackground
            )
        else MaterialTheme.colorScheme
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
            Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (day == null) {
                CircularProgressIndicator()
            } else {
                val morning = day!!.morning
                val evening = day!!.evening
                Column {
                    Text(dateString, style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Text(morning.name, style = MaterialTheme.typography.headlineMedium)
                }
                
                val isEastertide = morning.season == Season.EASTER || morning.season == Season.ASCENSION
                OfficeSection(
                    title = "Morning Prayer",
                    office = morning,
                    subtitle = null,
                    includePsalm95 = !isEastertide,
                    collectTab = morningTab,
                    onCollectTabSelected = { vm.morningCollectTab.value = it }
                )
                OfficeSection(
                    title = "Evening Prayer",
                    office = evening,
                    subtitle = if (evening.name != morning.name) evening.name else null,
                    includePsalm95 = false,
                    collectTab = eveningTab,
                    onCollectTabSelected = { vm.eveningCollectTab.value = it }
                )
            }
        }
        }
    }
}

@Composable
private fun OfficeSection(
    title: String,
    office: Office,
    subtitle: String?,
    includePsalm95: Boolean,
    collectTab: Int,
    onCollectTabSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary)
        if (subtitle != null)
            Text(subtitle, style = MaterialTheme.typography.titleMedium)

        val psalms = if (includePsalm95 && office.psalter.isNotBlank()) "95, ${office.psalter}"
                     else office.psalter
        OfficeRow("Psalm", psalms)
        OfficeRow("First Reading", office.firstReading)
        if (office.secondReading.isNotBlank()) OfficeRow("Second Reading", office.secondReading)
        
        if (office.collects.isNotEmpty()) {
            CollectChoiceBlock(office.collects, collectTab, onCollectTabSelected)
        }
    }
}

@Composable
private fun OfficeRow(label: String, value: String) {
    if (value.isBlank()) return
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CollectChoiceBlock(
    collects: Map<String, String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    if (collects.isEmpty()) return

    if (collects.size == 1) {
        val entry = collects.entries.first()
        OfficeRow("Collect (${entry.key})", entry.value)
        return
    }

    val keys = collects.keys.toList()
    Column {
        TabRow(selectedTabIndex = selectedTab) {
            keys.forEachIndexed { i, name ->
                Tab(selected = selectedTab == i, onClick = { onTabSelected(i) },
                    text = { Text(name, style = MaterialTheme.typography.labelMedium) })
            }
        }
        Spacer(Modifier.height(8.dp))
        val text = collects[keys[selectedTab]] ?: ""
        OfficeRow("Collect", text)
    }
}
