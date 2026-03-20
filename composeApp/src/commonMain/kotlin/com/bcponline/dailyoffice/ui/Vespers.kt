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

/** Returns (primary, alternate?) for the first vespers canticle slot. Null = use Magnificat only. */
private fun firstVespersCanticle(date: LocalDate, season: Season, rank: Rank): Pair<StringResource, StringResource?>? {
    // Feasts/Principals and non-Advent Sundays: Magnificat only (null = single canticle)
    if (rank >= Rank.FEAST && rank < Rank.HOLY_WEEK) return null
    return when (date.dayOfWeek) {
        DayOfWeek.SUNDAY -> when (season) {
            Season.ADVENT -> Res.string.spirit to Res.string.magnificat
            else          -> null // Magnificat first, Nunc Dimittis second
        }
        DayOfWeek.MONDAY    -> Res.string.redeemed to Res.string.moses
        DayOfWeek.TUESDAY   -> Res.string.lamb to Res.string.second_isaiah
        DayOfWeek.WEDNESDAY -> Res.string.adoption to Res.string.praise
        DayOfWeek.THURSDAY  -> Res.string.faith to Res.string.third_isaiah
        DayOfWeek.FRIDAY    -> Res.string.humility to Res.string.praise
        DayOfWeek.SATURDAY  -> Res.string.heavenly_city to Res.string.first_isaiah
        else                -> null
    }
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
    val firstCanticle = if (twoReadings) firstVespersCanticle(date, office.season, office.rank) else null
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY

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
            MarkdownText("**Second Reading:** ${office.secondReading}")
        } else {
            MarkdownText("**Reading:** ${office.secondReading}")
        }

        // Canticles
        if (!twoReadings || firstCanticle == null && !isSunday) {
            // Single canticle: Magnificat
            SectionHeader("Magnificat")
            MarkdownText(stringResource(Res.string.magnificat))
        } else if (firstCanticle == null) {
            // Normal Sunday: Magnificat then Nunc Dimittis
            SectionHeader("Magnificat")
            MarkdownText(stringResource(Res.string.magnificat))
            SectionHeader("Nunc Dimittis")
            MarkdownText(stringResource(Res.string.nunc_dimittis))
        } else if (office.season == Season.ADVENT && isSunday) {
            // Advent Sunday: Spirit (alt Magnificat), then Magnificat (alt Nunc Dimittis)
            CanticleSlot("The First Canticle", firstCanticle.first, firstCanticle.second)
            TabbedPane(
                mapOf("Suggested" to stringResource(Res.string.magnificat), "BCP" to stringResource(Res.string.nunc_dimittis)),
                heading = "The Second Canticle"
            )
        } else {
            // Weekday: first canticle from table, Magnificat second
            CanticleSlot("The First Canticle", firstCanticle.first, firstCanticle.second)
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
