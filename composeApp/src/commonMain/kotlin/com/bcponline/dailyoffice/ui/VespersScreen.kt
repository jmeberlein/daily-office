package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import dailyoffice.composeapp.generated.resources.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

// ── Vespers canticle catalogue ────────────────────────────────────────────────

private val MAGNIFICAT    = CanticleInfo(Res.string.magnificat,     "Magnificat",                  "Luke 1:46-55")
private val NUNC_DIMITTIS = CanticleInfo(Res.string.nunc_dimittis,  "Nunc Dimittis",               "Luke 2:29-32")
private val SPIRIT        = CanticleInfo(Res.string.spirit,         "Song of the Spirit",          "Revelation 22:12-17")
private val REDEEMED      = CanticleInfo(Res.string.redeemed,       "Song of the Redeemed",        "Revelation 15:3-4")
private val LAMB          = CanticleInfo(Res.string.lamb,           "Song to the Lamb",            "Revelation 4:11; 5:9-10,13")
private val ADOPTION      = CanticleInfo(Res.string.adoption,       "Song of Our Adoption",        "Ephesians 1:3-10")
private val FAITH         = CanticleInfo(Res.string.faith,          "Song of Faith",               "")
private val HUMILITY      = CanticleInfo(Res.string.humility,       "Song of Christ's Humility",   "Philippians 2:6-11")
private val HEAVENLY_CITY = CanticleInfo(Res.string.heavenly_city,  "Song of the Heavenly City",   "Revelation 21:22-26; 22:1-4")
// Shared with matins
private val MOSES_V       = CanticleInfo(Res.string.moses,          "Song of Moses",               "Exodus 15:1-6,11-13,17-18")
private val SECOND_ISAIAH_V = CanticleInfo(Res.string.second_isaiah, "2nd Song of Isaiah",         "Isaiah 55:6-11")
private val THIRD_ISAIAH_V  = CanticleInfo(Res.string.third_isaiah,  "3rd Song of Isaiah",         "Isaiah 60:1-3,11a,14c,18-19")
private val FIRST_ISAIAH_V  = CanticleInfo(Res.string.first_isaiah,  "1st Song of Isaiah",         "Isaiah 12:2-6")
private val PRAISE_V        = CanticleInfo(Res.string.praise,         "Song of Praise",             "Three Children 29-34")

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun VespersScreen(vm: OfficeViewModel = viewModel { OfficeViewModel() }) {
    val state by vm.state.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    when (val s = state) {
        is OfficeUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is OfficeUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text("Failed to load: ${s.message}")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.load(today, settings) }) { Text("Retry") }
            }
        }
        is OfficeUiState.Success -> VespersContent(
            office = s.day.evening,
            date = s.date,
            settings = settings
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun VespersContent(office: Office, date: LocalDate, settings: OfficeSettings) {
    val colors = officeColorsFor(office)
    OfficeTheme(colors) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // ── Invitatory ────────────────────────────────────────────
            OfficeSectionHeader("Invitatory and Psalter", colors.primary)
            LiturgyLine("Officiant", "O God, make speed to save us.", colors.onBg)
            LiturgyLine("People", "O Lord, make haste to help us.", colors.onBg)
            LiturgyLine("All", "Glory to the Father, and to the Son, and to the Holy Spirit: as it was in the beginning, is now, and will be for ever. Amen.${if (office.season != Season.LENT) " Alleluia, alleluia." else ""}", colors.onBg)
            Spacer(Modifier.height(16.dp))
            PhosHilaronBlock(colors.primary, colors.onBg)
            Spacer(Modifier.height(16.dp))

            OfficeSubHeader("Appointed Psalms: ${office.psalter.ifBlank { "—" }}", colors.primary)
            Spacer(Modifier.height(4.dp))
            OfficeRubric("Glory to the Father, and to the Son, and to the Holy Spirit: *\n    as it was in the beginning, is now, and will be for ever. Amen.", colors.onBg)

            Spacer(Modifier.height(24.dp))

            // ── The Lessons ───────────────────────────────────────────
            OfficeSectionHeader("The Lessons", colors.primary)
            val hasFirstReading = office.firstReading.isNotBlank()
            val hasSecondReading = office.secondReading.isNotBlank()
            val canticles = vespersCanticlesFor(office, date)

            if (hasFirstReading) {
                ReadingLabel("First Reading", office.firstReading, colors.onBg)
                Spacer(Modifier.height(12.dp))
                if (!settings.condensed) {
                    CanticleBlock(canticles.first, colors.primary, colors.onBg)
                    Spacer(Modifier.height(12.dp))
                }
            }
            if (hasSecondReading) {
                ReadingLabel(if (hasFirstReading) "Second Reading" else "The Reading", office.secondReading, colors.onBg)
                Spacer(Modifier.height(12.dp))
            }
            CanticleBlock(canticles.second, colors.primary, colors.onBg)

            if (!settings.condensed) {
                Spacer(Modifier.height(16.dp))
                OfficeSubHeader("The Apostles' Creed", colors.primary)
                Spacer(Modifier.height(8.dp))
                OfficeBodyText(apostlesCreed, colors.onBg)
            }

            Spacer(Modifier.height(24.dp))

            // ── The Prayers ───────────────────────────────────────────
            OfficeSectionHeader("The Prayers", colors.primary)
            if (!settings.condensed) {
                LiturgyLine("Officiant", "The Lord be with you.", colors.onBg)
                LiturgyLine("People", "And also with you.", colors.onBg)
                LiturgyLine("Officiant", "Let us pray.", colors.onBg)
                Spacer(Modifier.height(12.dp))
            }
            OfficeBodyText(ourFather, colors.onBg)

            if (!settings.condensed) {
                Spacer(Modifier.height(16.dp))
                OfficeSubHeader("Suffrages", colors.primary)
                Spacer(Modifier.height(8.dp))
                VespersSuffragesBlock(colors.onBg, colors.primary)
            }

            Spacer(Modifier.height(16.dp))
            OfficeSubHeader("Collect", colors.primary)
            Spacer(Modifier.height(8.dp))
            if (office.collect.isNotBlank()) OfficeBodyText(office.collect, colors.onBg)

            if (!settings.condensed) {
                Spacer(Modifier.height(16.dp))
                OfficeSubHeader("Intercessions", colors.primary)
                Spacer(Modifier.height(8.dp))
                VespersIntercessionsBlock(colors.onBg, colors.primary)
            }

            Spacer(Modifier.height(24.dp))

            // ── Closing ───────────────────────────────────────────────
            LiturgyLine("Officiant", "Let us bless the Lord.", colors.onBg)
            LiturgyLine("People", if (office.season == Season.EASTER) "Thanks be to God. Alleluia, alleluia." else "Thanks be to God.", colors.onBg)

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Canticle selection ────────────────────────────────────────────────────────

/** Returns (firstCanticle options, secondCanticle options). Second is always Magnificat unless Sunday/Feast. */
private fun vespersCanticlesFor(office: Office, date: LocalDate): Pair<List<CanticleInfo>, List<CanticleInfo>> {
    val isHighFeast = office.rank == Rank.FEAST || office.rank == Rank.PRINCIPAL
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY

    // Only one reading → just Magnificat
    if (office.firstReading.isBlank()) return Pair(emptyList(), listOf(MAGNIFICAT))

    val second = if (isHighFeast || isSunday) listOf(NUNC_DIMITTIS) else listOf(MAGNIFICAT)

    val first: List<CanticleInfo> = when {
        isHighFeast || isSunday -> when {
            office.season == Season.ADVENT -> listOf(SPIRIT, MAGNIFICAT)
            else -> listOf(MAGNIFICAT)
        }
        else -> when (date.dayOfWeek) {
            DayOfWeek.MONDAY    -> listOf(REDEEMED, MOSES_V)
            DayOfWeek.TUESDAY   -> listOf(LAMB, SECOND_ISAIAH_V)
            DayOfWeek.WEDNESDAY -> listOf(ADOPTION, PRAISE_V)
            DayOfWeek.THURSDAY  -> listOf(FAITH, THIRD_ISAIAH_V)
            DayOfWeek.FRIDAY    -> listOf(HUMILITY, PRAISE_V)
            DayOfWeek.SATURDAY  -> listOf(HEAVENLY_CITY, FIRST_ISAIAH_V)
            else                -> listOf(MAGNIFICAT)
        }
    }
    return Pair(first, second)
}

// ── Local composables ─────────────────────────────────────────────────────────

@Composable
private fun PhosHilaronBlock(primary: Color, textColor: Color) {
    var selected by remember { mutableIntStateOf(0) }
    TabRow(
        selectedTabIndex = selected,
        containerColor = Color.Transparent,
        contentColor = primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("Prose", "Metrical").forEachIndexed { i, title ->
            Tab(selected == i, onClick = { selected = i },
                text = { Text(title, style = MaterialTheme.typography.labelMedium) })
        }
    }
    Spacer(Modifier.height(8.dp))
    val info = if (selected == 0)
        CanticleInfo(Res.string.phos_hilaron, "Phos Hilaron", "")
    else
        CanticleInfo(Res.string.phos_hilaron_metrical, "Phos Hilaron", "")
    CanticleBlock(listOf(info), primary, textColor)
}

@Composable
private fun ReadingLabel(label: String, citation: String, textColor: Color) {
    Text(
        buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
            append("$label:  ")
            pop()
            append(citation)
        },
        color = textColor,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun VespersSuffragesBlock(textColor: Color, primary: Color) {
    var selected by remember { mutableIntStateOf(0) }
    TabRow(
        selectedTabIndex = selected,
        containerColor = Color.Transparent,
        contentColor = primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("Option A", "Option B").forEachIndexed { i, title ->
            Tab(selected == i, onClick = { selected = i },
                text = { Text(title, style = MaterialTheme.typography.labelMedium) })
        }
    }
    Spacer(Modifier.height(8.dp))
    if (selected == 0) {
        suffrageALines.forEach { (v, r) ->
            LiturgyLine("V.", v, textColor)
            LiturgyLine("R.", r, textColor)
        }
    } else {
        vespersSuffrageBLines.forEach { (petition, response) ->
            Text(petition, color = textColor, style = MaterialTheme.typography.bodyMedium)
            Text(response, color = textColor, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun VespersIntercessionsBlock(textColor: Color, primary: Color) {
    IntercessionsBlock(textColor, primary, vespersPrayerForMission)
}

// ── Static texts ──────────────────────────────────────────────────────────────

private val vespersSuffrageBLines = listOf(
    "That this evening may be holy, good, and peaceful," to "We entreat you, O Lord.",
    "That your holy angels may lead us in paths of peace and goodwill," to "We entreat you, O Lord.",
    "That we may be pardoned and forgiven for our sins and offenses," to "We entreat you, O Lord.",
    "That there may be peace to your Church and to the whole world," to "We entreat you, O Lord.",
    "That we may depart this life in your faith and fear, and not be condemned before the great judgment seat of Christ," to "We entreat you, O Lord.",
    "That we may be bound together by your Holy Spirit in the communion of [________ and] all your saints, entrusting one another and all our life to Christ," to "We entreat you, O Lord."
)

private val vespersPrayerForMission = "Keep watch, dear Lord, with those who work, or watch, or weep this night, and give your angels charge over those who sleep. Tend the sick, Lord Christ; give rest to the weary, bless the dying, soothe the suffering, pity the afflicted, shield the joyous; and all for your love's sake. Amen."

