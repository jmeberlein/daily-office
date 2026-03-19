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
fun Compline(date: LocalDate, day: LiturgicalDay, condensed: Boolean) {
    val office = day.evening
    Column(modifier = Modifier.fillMaxWidth().background(office.color.background).padding(16.dp)) {
        Text("Compline — ${office.name}", style = MaterialTheme.typography.headlineSmall)
        if (!condensed) {
            if (office.season == Season.LENT) {
                MarkdownText(stringResource(Res.string.versicle_vespers_lent))
            } else {
                MarkdownText(stringResource(Res.string.versicle_vespers))
            }
        }

        MarkdownText(stringResource(Res.string.psalm_134))
        MarkdownText(stringResource(Res.string.reading_compline))

        if (!condensed) {
            MarkdownText(stringResource(Res.string.suffrages_compline))
        }

        MarkdownText(stringResource(Res.string.our_father))
        MarkdownText(stringResource(Res.string.collect_compline))

        if (!condensed) {
            if (office.season == Season.EASTER) {
                MarkdownText(stringResource(Res.string.antiphon_compline_easter))
            } else {
                MarkdownText(stringResource(Res.string.antiphon_compline))
            }
        }
        MarkdownText(stringResource(Res.string.nunc_dimittis))
        if (!condensed) {
            if (office.season == Season.EASTER) {
                MarkdownText(stringResource(Res.string.antiphon_compline_easter))
            } else {
                MarkdownText(stringResource(Res.string.antiphon_compline))
            }
        }
        MarkdownText(stringResource(Res.string.closing_versicle))
    }
}
