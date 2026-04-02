package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.layout.*
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

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SettingToggle(
            label = "Always use two readings",
            description = "Pull from the alternate year to provide two readings every day",
            checked = forceTwoReadings,
            onCheckedChange = vm::setForceTwoReadings
        )
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
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
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        SettingToggle(
            label = "First canticle",
            description = "Include a canticle between the two readings at Morning Prayer",
            checked = showFirstCanticle,
            onCheckedChange = vm::setShowFirstCanticle
        )
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
