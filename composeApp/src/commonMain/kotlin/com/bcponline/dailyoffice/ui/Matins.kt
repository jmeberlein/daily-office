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

/** Returns (primary, alternate?) canticle resources for the first canticle slot. */
private fun firstCanticles(date: LocalDate, season: Season, rank: Rank): Pair<StringResource, StringResource?> {
    // Feasts and Principals (excluding Holy Week / Easter weekdays, which are PRINCIPAL but treated as feria)
    if (rank >= Rank.FEAST && rank < Rank.HOLY_WEEK) {
        return Res.string.te_deum to null
    }
    return when (date.dayOfWeek) {
        DayOfWeek.SUNDAY -> when (season) {
            Season.ADVENT   -> Res.string.third_isaiah to null
            Season.CHRISTMAS -> Res.string.hannah to Res.string.te_deum
            Season.LENT     -> Res.string.hosea to Res.string.kyrie_pantokrator
            Season.EASTER   -> Res.string.moses to null
            else            -> Res.string.te_deum to null
        }
        DayOfWeek.MONDAY    -> Res.string.wisdom to Res.string.first_isaiah
        DayOfWeek.TUESDAY   -> Res.string.pilgrimage to Res.string.praise
        DayOfWeek.WEDNESDAY -> if (season == Season.LENT)
            Res.string.ezekiel to Res.string.kyrie_pantokrator
        else
            Res.string.ezekiel to Res.string.third_isaiah
        DayOfWeek.THURSDAY  -> Res.string.judith to Res.string.moses
        DayOfWeek.FRIDAY    -> Res.string.kyrie_pantokrator to Res.string.second_isaiah
        DayOfWeek.SATURDAY  -> Res.string.praise to null
        else                -> Res.string.te_deum to null
    }
}

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
        when (office.season) {
            Season.EASTER -> { SectionHeader("Pascha Nostrum"); MarkdownText(stringResource(Res.string.pascha_nostrum)) }
            Season.LENT   -> { SectionHeader("Psalm 95"); MarkdownText(stringResource(Res.string.psalm_95_lent)) }
            else          -> { SectionHeader("Psalm 95"); MarkdownText(stringResource(Res.string.psalm_95)) }
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
                val (primary, alternate) = firstCanticles(date, office.season, office.rank)
                if (alternate != null) {
                    TabbedPane(
                        mapOf(
                            "Suggested" to stringResource(primary),
                            "BCP" to stringResource(alternate)
                        ),
                        heading = "The First Canticle"
                    )
                } else {
                    SectionHeader("The First Canticle")
                    MarkdownText(stringResource(primary))
                }
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
