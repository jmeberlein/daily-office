package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun IntercessionsBlock(selectedTab: Int, missionPrayer: StringResource, onTabSelected: (Int) -> Unit) {
    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { onTabSelected(0) },
                text = { Text("Form I", style = MaterialTheme.typography.labelMedium) })
            Tab(selected = selectedTab == 1, onClick = { onTabSelected(1) },
                text = { Text("Form III", style = MaterialTheme.typography.labelMedium) })
            Tab(selected = selectedTab == 2, onClick = { onTabSelected(2) },
                text = { Text("Form IV", style = MaterialTheme.typography.labelMedium) })
            Tab(selected = selectedTab == 3, onClick = { onTabSelected(3) },
                text = { Text("Prayer for Mission", style = MaterialTheme.typography.labelMedium) })
        }
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(when (selectedTab) {
                0    -> ServiceTexts.INTERCESSIONS_I
                1    -> ServiceTexts.INTERCESSIONS_III
                2    -> ServiceTexts.INTERCESSIONS_IV
                else -> missionPrayer
            }),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
