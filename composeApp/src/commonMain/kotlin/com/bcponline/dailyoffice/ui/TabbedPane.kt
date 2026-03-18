package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays a tabbed pane where each key in [sections] is a tab label
 * and each value is Markdown content rendered via [MarkdownText].
 */
@Composable
fun TabbedPane(sections: Map<String, String>, modifier: Modifier = Modifier) {
    val keys = sections.keys.toList()
    var selectedIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        ScrollableTabRow(selectedTabIndex = selectedIndex, edgePadding = 0.dp) {
            keys.forEachIndexed { index, title ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    text = { Text(title) }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            sections[keys[selectedIndex]]?.let { MarkdownText(it) }
        }
    }
}
