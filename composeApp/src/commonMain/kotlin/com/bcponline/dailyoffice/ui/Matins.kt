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
import com.bcponline.dailyoffice.model.Season
import dailyoffice.composeapp.generated.resources.*
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun Matins(date: LocalDate, day: LiturgicalDay, condensed: Boolean) {
    val office = day.morning
    Column(modifier = Modifier.fillMaxWidth().background(office.color.background).padding(16.dp)) {
        Text("Matins — ${office.name}", style = MaterialTheme.typography.headlineSmall)
        if (!condensed) {
            if (office.season == Season.LENT) {
                MarkdownText(stringResource(Res.string.versicle_matins_lent))
            } else {
                MarkdownText(stringResource(Res.string.versicle_matins))
            }
        }

        if (office.season == Season.EASTER) {
            MarkdownText(stringResource(Res.string.pascha_nostrum))
        } else {
            MarkdownText(stringResource(Res.string.psalm_95))
        }

        MarkdownText(String.format(stringResource(Res.string.psalms_appointed), office.psalter))
        MarkdownText(stringResource(Res.string.lessons_intro))
        if (office.firstReading.isEmpty()) {
            MarkdownText(String.format(stringResource(Res.string.first_reading), office.secondReading))
        } else {
            MarkdownText(String.format(stringResource(Res.string.first_reading), office.firstReading))
            MarkdownText(String.format(stringResource(Res.string.second_reading), office.secondReading))
        }
        MarkdownText(stringResource(Res.string.benedictus))

        if (!condensed) {
            MarkdownText(stringResource(Res.string.creed))
        }

        MarkdownText(stringResource(Res.string.prayers_intro))
        MarkdownText(stringResource(Res.string.our_father))

        if (!condensed) {
            TabbedPane(mapOf(
                Pair("Suffrages A", stringResource(Res.string.suffrages_a)),
                Pair("Suffrages B", stringResource(Res.string.suffrages_b_matins))
            ))
        }

        MarkdownText(String.format(stringResource(Res.string.collect), office.collect))

        if (office.season == Season.EASTER) {
            MarkdownText(stringResource(Res.string.closing_versicle_easter))
        } else {
            MarkdownText(stringResource(Res.string.closing_versicle))
        }
    }
}
