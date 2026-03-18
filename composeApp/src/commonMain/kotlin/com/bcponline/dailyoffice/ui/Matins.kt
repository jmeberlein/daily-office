package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.model.LiturgicalDay
import dailyoffice.composeapp.generated.resources.Res
import dailyoffice.composeapp.generated.resources.suffrages_a
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun Matins(date: LocalDate, day: LiturgicalDay, condensed: Boolean) {
    val office = day.morning
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Matins — ${office.name}", style = MaterialTheme.typography.headlineSmall)
        Text("Date: $date", style = MaterialTheme.typography.labelMedium)
        if (!condensed) {
            Text("Psalm ${office.psalter}", style = MaterialTheme.typography.titleMedium)
            Text("First Reading: ${office.firstReading}")
            Text("Second Reading: ${office.secondReading}")
        }
        MarkdownText(office.collect)
        MarkdownText(stringResource(Res.string.suffrages_a))
    }
}
