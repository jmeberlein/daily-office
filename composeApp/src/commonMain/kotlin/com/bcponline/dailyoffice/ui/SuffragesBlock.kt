package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SuffragesBlock(selectedTab: Int, optionB: StringResource, onTabSelected: (Int) -> Unit) {
    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { onTabSelected(0) },
                text = { Text("A", style = MaterialTheme.typography.labelMedium) })
            Tab(selected = selectedTab == 1, onClick = { onTabSelected(1) },
                text = { Text("B", style = MaterialTheme.typography.labelMedium) })
        }
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(if (selectedTab == 0) ServiceTexts.SUFFRAGES_A else optionB),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
