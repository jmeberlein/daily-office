package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel { SettingsViewModel() }) {
    val forceTwoReadings by vm.forceTwoReadings.collectAsStateWithLifecycle()
    val useOptionalFeasts by vm.useOptionalFeasts.collectAsStateWithLifecycle()
    val useExtraFeasts by vm.useExtraFeasts.collectAsStateWithLifecycle()
    val showFirstCanticle by vm.showFirstCanticle.collectAsStateWithLifecycle()
    val showCreed by vm.showCreed.collectAsStateWithLifecycle()
    val showSuffrages by vm.showSuffrages.collectAsStateWithLifecycle()
    val showIntercessions by vm.showIntercessions.collectAsStateWithLifecycle()

    var tab by remember { mutableStateOf(0) }
    Column {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Settings") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Order of Service") })
        }
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (tab) {
                0 -> {
                    SettingToggle(
                        label = "Always use two readings",
                        description = "Pull from the alternate year to provide two readings every day",
                        checked = forceTwoReadings,
                        onCheckedChange = vm::setForceTwoReadings
                    )
                    SettingToggle(
                        label = "Optional feasts",
                        description = "Include optional feasts from the BCP calendar",
                        checked = useOptionalFeasts,
                        onCheckedChange = vm::setUseOptionalFeasts
                    )
                    SettingToggle(
                        label = "Additional saints",
                        description = "Add more saints to the calendar (requires optional feasts)",
                        checked = useExtraFeasts,
                        onCheckedChange = vm::setUseExtraFeasts,
                        enabled = useOptionalFeasts
                    )
                }
                1 -> {
                    ServiceItem("Invitatory", enabled = false, checked = true, onCheckedChange = {})
                    ServiceItem("Psalms", enabled = false, checked = true, onCheckedChange = {})
                    ServiceItem("Readings", enabled = false, checked = true, onCheckedChange = {})
                    ServiceItem("First canticle", enabled = true, checked = showFirstCanticle, onCheckedChange = vm::setShowFirstCanticle)
                    ServiceItem("Second canticle / Benedictus / Magnificat", enabled = false, checked = true, onCheckedChange = {})
                    ServiceItem("Apostles' Creed", enabled = true, checked = showCreed, onCheckedChange = vm::setShowCreed)
                    ServiceItem("Lord's Prayer", enabled = false, checked = true, onCheckedChange = {})
                    ServiceItem("Suffrages", enabled = true, checked = showSuffrages, onCheckedChange = vm::setShowSuffrages)
                    ServiceItem("Collect", enabled = false, checked = true, onCheckedChange = {})
                    ServiceItem("Intercessions", enabled = true, checked = showIntercessions, onCheckedChange = vm::setShowIntercessions)
                    ServiceItem("Closing Versicle", enabled = false, checked = true, onCheckedChange = {})
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
            Text(description, style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f))
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

@Composable
private fun ServiceItem(
    label: String,
    enabled: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}
