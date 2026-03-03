package com.bcponline.dailyoffice.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionPanel(options: Map<String, String>, modifier: Modifier = Modifier) {
    if (options.isEmpty()) return

    var selectedTabKey by remember { mutableStateOf(options.keys.first()) }
    val keys = options.keys.toList()

    Column(modifier = modifier) {
        ScrollableTabRow(
            selectedTabIndex = keys.indexOf(selectedTabKey),
            edgePadding = 0.dp
        ) {
            keys.forEach { title ->
                Tab(
                    selected = selectedTabKey == title,
                    onClick = { selectedTabKey = title },
                    text = { Text(title) }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(text = options[selectedTabKey] ?: "")
        }
    }
}
