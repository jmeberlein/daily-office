package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.model.LiturgicalDay
import kotlinx.datetime.LocalDate

@Composable
fun Vespers(date: LocalDate, day: LiturgicalDay, condensed: Boolean) {
    val office = day.evening
    Column(modifier = Modifier.fillMaxSize().background(office.color.background).padding(16.dp)) {
        Text("Vespers — ${office.name}", style = MaterialTheme.typography.headlineSmall)
        Text("Date: $date", style = MaterialTheme.typography.labelMedium)
        if (!condensed) {
            Text("Psalm ${office.psalter}", style = MaterialTheme.typography.titleMedium)
            Text("First Reading: ${office.firstReading}")
            Text("Second Reading: ${office.secondReading}")
        }
        MarkdownText(office.collect)
    }
}
