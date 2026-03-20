package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays a tabbed pane where each key in [sections] is a tab label
 * and each value is Markdown content rendered via [MarkdownText].
 * An optional [heading] is shown above the tabs in rubric style.
 */
@Composable
fun TabbedPane(sections: Map<String, String>, heading: String? = null, modifier: Modifier = Modifier) {
    val keys = sections.keys.toList()
    var selectedIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (heading != null) {
            MarkdownText("*$heading*")
        }
        ScrollableTabRow(selectedTabIndex = selectedIndex, edgePadding = 0.dp) {
            keys.forEachIndexed { index, title ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    text = { Text(title) }
                )
            }
        }
        sections[keys[selectedIndex]]?.let { MarkdownText(it) }
    }
}
