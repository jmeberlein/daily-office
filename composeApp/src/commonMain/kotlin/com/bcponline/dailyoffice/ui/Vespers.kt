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
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import dailyoffice.composeapp.generated.resources.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private fun weekdayCanticle(date: LocalDate): Pair<StringResource, StringResource?> =
    when (date.dayOfWeek) {
        DayOfWeek.MONDAY    -> Res.string.redeemed to Res.string.moses
        DayOfWeek.TUESDAY   -> Res.string.lamb to Res.string.second_isaiah
        DayOfWeek.WEDNESDAY -> Res.string.adoption to Res.string.praise
        DayOfWeek.THURSDAY  -> Res.string.faith to Res.string.third_isaiah
        DayOfWeek.FRIDAY    -> Res.string.humility to Res.string.praise
        DayOfWeek.SATURDAY  -> Res.string.heavenly_city to Res.string.first_isaiah
        else                -> Res.string.magnificat to null
    }

@Composable
private fun CanticleSlot(heading: String, primary: StringResource, alternate: StringResource?) {
    if (alternate != null) {
        TabbedPane(
            mapOf("Suggested" to stringResource(primary), "BCP" to stringResource(alternate)),
            heading = heading
        )
    } else {
        SectionHeader(heading)
        MarkdownText(stringResource(primary))
    }
}

@Composable
fun Vespers(date: LocalDate, day: LiturgicalDay, condensed: Boolean) {
    val office = day.evening
    val twoReadings = office.firstReading.isNotEmpty()
    val isSundayOrFeast = date.dayOfWeek == DayOfWeek.SUNDAY || (office.rank < Rank.HOLY_WEEK && office.rank >= Rank.FEAST)

    Column(modifier = Modifier.fillMaxWidth().background(office.color.background).padding(16.dp)) {
        Text("Vespers — ${office.name}", style = MaterialTheme.typography.headlineSmall)

        // Versicle
        if (!condensed) {
            SectionHeader("The Versicle")
            if (office.season == Season.LENT) {
                MarkdownText(stringResource(Res.string.versicle_vespers_lent))
            } else {
                MarkdownText(stringResource(Res.string.versicle_vespers))
            }
        }

        // Phos Hilaron
        TabbedPane(
            mapOf(
                "Prose" to stringResource(Res.string.phos_hilaron),
                "Metrical" to stringResource(Res.string.phos_hilaron_metrical)
            ),
            heading = "O Gracious Light"
        )

        // Psalter
        SectionHeader("The Psalter")
        MarkdownText("Psalms: ${office.psalter}")
        MarkdownText(stringResource(Res.string.psalms_appointed))

        // Lessons
        SectionHeader("The Lessons")
        MarkdownText(stringResource(Res.string.lessons_intro))
        if (twoReadings) {
            MarkdownText("**First Reading:** ${office.firstReading}")
        } else {
            MarkdownText("**Reading:** ${office.secondReading}")
        }

        // Canticles
        if (!twoReadings || isSundayOrFeast) {
            SectionHeader("Magnificat")
            MarkdownText(stringResource(Res.string.magnificat))
            if (twoReadings) {
                MarkdownText("**Second Reading:** ${office.secondReading}")
                SectionHeader("Nunc Dimittis")
                MarkdownText(stringResource(Res.string.nunc_dimittis))
            }
        } else {
            val (primary, alternate) = weekdayCanticle(date)
            CanticleSlot("The First Canticle", primary, alternate)
            MarkdownText("**Second Reading:** ${office.secondReading}")
            SectionHeader("Magnificat")
            MarkdownText(stringResource(Res.string.magnificat))
        }

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
                    "Suffrages B" to stringResource(Res.string.suffrages_b_vespers)
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
