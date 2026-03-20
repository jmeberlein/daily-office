package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
    val twoReadings = office.firstReading.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth().background(office.color.background).padding(16.dp)) {
        Text("Matins — ${office.name}", style = MaterialTheme.typography.headlineSmall)

        // Invitatory
        SectionHeader("The Invitatory")
        if (!condensed) {
            if (office.season == Season.LENT) {
                MarkdownText(stringResource(Res.string.versicle_matins_lent))
            } else {
                MarkdownText(stringResource(Res.string.versicle_matins))
            }
        }
        if (office.season == Season.EASTER) {
            SectionHeader("Pascha Nostrum")
            MarkdownText(stringResource(Res.string.pascha_nostrum))
        } else if (office.season == Season.LENT) {
            SectionHeader("Psalm 95")
            MarkdownText(stringResource(Res.string.psalm_95_lent))
        } else {
            SectionHeader("Psalm 95")
            MarkdownText(stringResource(Res.string.psalm_95))
        }

        // Psalter
        SectionHeader("The Psalter")
        MarkdownText("Psalms: ${office.psalter}")
        MarkdownText(stringResource(Res.string.psalms_appointed))

        // Lessons
        SectionHeader("The Lessons")
        MarkdownText(stringResource(Res.string.lessons_intro))
        if (twoReadings) {
            MarkdownText("**First Reading:** ${office.firstReading}")
            if (!condensed) {
                SectionHeader("The First Canticle")
                MarkdownText(stringResource(Res.string.first_canticle_placeholder))
            }
            MarkdownText("**Second Reading:** ${office.secondReading}")
        } else {
            MarkdownText("**Reading:** ${office.secondReading}")
        }
        SectionHeader("Benedictus")
        MarkdownText(stringResource(Res.string.benedictus))

        if (!condensed) {
            SectionHeader("Apostles' Creed")
            MarkdownText(stringResource(Res.string.creed))
        }

        // Prayers
        SectionHeader("The Prayers")
        MarkdownText(stringResource(Res.string.prayers_intro))
        MarkdownText(stringResource(Res.string.our_father))
        if (!condensed) {
            TabbedPane(
                mapOf(
                    "Suffrages A" to stringResource(Res.string.suffrages_a),
                    "Suffrages B" to stringResource(Res.string.suffrages_b_matins)
                ),
                heading = "The Suffrages"
            )
        }
        SectionHeader("The Collect")
        MarkdownText(String.format(stringResource(Res.string.collect), office.collect.replace("\n", "  \n")))

        if (office.season == Season.EASTER) {
            MarkdownText(stringResource(Res.string.closing_versicle_easter))
        } else {
            MarkdownText(stringResource(Res.string.closing_versicle))
        }
    }
}
