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

// ── Matins canticle catalogue ─────────────────────────────────────────────────

private val BENEDICTUS    = CanticleInfo(Res.string.benedictus,        "Benedictus",              "Luke 1:68-79")
private val TE_DEUM       = CanticleInfo(Res.string.te_deum,           "Te Deum Laudamus",        "")
private val FIRST_ISAIAH  = CanticleInfo(Res.string.first_isaiah,      "1st Song of Isaiah",      "Isaiah 12:2-6")
private val SECOND_ISAIAH = CanticleInfo(Res.string.second_isaiah,     "2nd Song of Isaiah",      "Isaiah 55:6-11")
private val THIRD_ISAIAH  = CanticleInfo(Res.string.third_isaiah,      "3rd Song of Isaiah",      "Isaiah 60:1-3,11a,14c,18-19")
private val WILDERNESS    = CanticleInfo(Res.string.wilderness,        "Song of the Wilderness",  "Isaiah 35:1-7,10")
private val HANNAH        = CanticleInfo(Res.string.hannah,            "Song of Hannah",          "1 Samuel 2:1-18")
private val HOSEA         = CanticleInfo(Res.string.hosea,             "Song of Hosea",           "Hosea 6:1-3")
private val KYRIE         = CanticleInfo(Res.string.kyrie_pantokrator, "Kyrie Pantokrator",       "Manasseh 1-2,4,6-7,11-15")
private val WISDOM        = CanticleInfo(Res.string.wisdom,            "Song of Wisdom",          "Wisdom 10:15-19,20b-21")
private val PILGRIMAGE    = CanticleInfo(Res.string.pilgrimage,        "Song of Pilgrimage",      "Sirach 51:13-16,20b-22")
private val PRAISE        = CanticleInfo(Res.string.praise,            "Song of Praise",          "Three Children 29-34")
private val EZEKIEL       = CanticleInfo(Res.string.ezekiel,           "Song of Ezekiel",         "Ezekiel 36:24-28")
private val JUDITH        = CanticleInfo(Res.string.judith,            "Song of Judith",          "Judith 16:13-16")
private val MOSES         = CanticleInfo(Res.string.moses,             "Song of Moses",           "Exodus 15:1-6,11-13,17-18")

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun MatinsScreen(vm: OfficeViewModel = viewModel { OfficeViewModel() }) {
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
        is OfficeUiState.Success -> MatinsContent(
            office = s.day.morning,
            date = s.date,
            settings = settings
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun MatinsContent(office: Office, date: LocalDate, settings: OfficeSettings) {
    val colors = officeColorsFor(office)
    OfficeTheme(colors) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            OfficeSectionHeader("Invitatory and Psalter", colors.primary)
            LiturgyLine("Officiant", "Lord, open our lips.", colors.onBg)
            LiturgyLine("People", "And our mouth shall proclaim your praise.", colors.onBg)
            LiturgyLine("All", "Glory to the Father, and to the Son, and to the Holy Spirit: as it was in the beginning, is now, and will be for ever. Amen.${if (office.season != Season.LENT) " Alleluia, alleluia." else ""}", colors.onBg)
            Spacer(Modifier.height(16.dp))
            CanticleBlock(openingCanticleFor(office), colors.primary, colors.onBg)
            Spacer(Modifier.height(16.dp))

            OfficeSubHeader("Appointed Psalms: ${office.psalter.ifBlank { "—" }}", colors.primary)
            Spacer(Modifier.height(4.dp))
            OfficeRubric("Glory to the Father, and to the Son, and to the Holy Spirit: *\n    as it was in the beginning, is now, and will be for ever. Amen.", colors.onBg)

            Spacer(Modifier.height(24.dp))

            // ── The Lessons ───────────────────────────────────────────
            OfficeSectionHeader("The Lessons", colors.primary)
            val hasFirstReading = office.firstReading.isNotBlank()
            val hasSecondReading = office.secondReading.isNotBlank()

            if (hasFirstReading) {
                ReadingLabel("First Reading", office.firstReading, colors.onBg)
                Spacer(Modifier.height(12.dp))
                if (!settings.condensed) {
                    CanticleBlock(firstCanticleFor(office, date), colors.primary, colors.onBg)
                    Spacer(Modifier.height(12.dp))
                }
            }
            if (hasSecondReading) {
                ReadingLabel(if (hasFirstReading) "Second Reading" else "The Reading", office.secondReading, colors.onBg)
                Spacer(Modifier.height(12.dp))
            }
            CanticleBlock(listOf(BENEDICTUS), colors.primary, colors.onBg)

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
                SuffragesBlock(colors.onBg, colors.primary)
            }

            Spacer(Modifier.height(16.dp))
            OfficeSubHeader("Collect", colors.primary)
            Spacer(Modifier.height(8.dp))
            if (office.collect.isNotBlank()) OfficeBodyText(office.collect, colors.onBg)

            if (!settings.condensed) {
                Spacer(Modifier.height(16.dp))
                OfficeSubHeader("Intercessions", colors.primary)
                Spacer(Modifier.height(8.dp))
                IntercessionsBlock(colors.onBg, colors.primary, matinsPrayerForMission)
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

private fun openingCanticleFor(office: Office): List<CanticleInfo> = when {
    office.season == Season.EASTER -> listOf(CanticleInfo(Res.string.pascha_nostrum, "Pascha Nostrum", "1 Cor 5:7-8; Rom 6:9-11; 1 Cor 15:20-22"))
    office.season == Season.LENT   -> listOf(CanticleInfo(Res.string.psalm_95_lent,  "Psalm 95 (Venite)", ""))
    else                           -> listOf(CanticleInfo(Res.string.psalm_95,        "Psalm 95 (Venite)", ""))
}

private fun firstCanticleFor(office: Office, date: LocalDate): List<CanticleInfo> {
    if (office.rank == Rank.FEAST || office.rank == Rank.PRINCIPAL) return listOf(TE_DEUM)
    return when {
        office.season == Season.ADVENT    && date.dayOfWeek == DayOfWeek.SUNDAY -> listOf(THIRD_ISAIAH, WILDERNESS)
        office.season == Season.CHRISTMAS && date.dayOfWeek == DayOfWeek.SUNDAY -> listOf(HANNAH, TE_DEUM)
        office.season == Season.LENT      && date.dayOfWeek == DayOfWeek.SUNDAY -> listOf(HOSEA, KYRIE)
        else -> when (date.dayOfWeek) {
            DayOfWeek.MONDAY    -> listOf(WISDOM, FIRST_ISAIAH)
            DayOfWeek.TUESDAY   -> listOf(PILGRIMAGE, PRAISE)
            DayOfWeek.WEDNESDAY -> if (office.season == Season.LENT) listOf(EZEKIEL, KYRIE) else listOf(EZEKIEL, THIRD_ISAIAH)
            DayOfWeek.THURSDAY  -> listOf(JUDITH, MOSES)
            DayOfWeek.FRIDAY    -> listOf(KYRIE, SECOND_ISAIAH)
            DayOfWeek.SATURDAY  -> listOf(PRAISE)
            else                -> listOf(TE_DEUM)
        }
    }
}

// ── Local composables ─────────────────────────────────────────────────────────

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
private fun SuffragesBlock(textColor: Color, primary: Color) {
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
    val lines = if (selected == 0) suffrageALines else suffrageBLines
    lines.forEach { (v, r) ->
        LiturgyLine("V.", v, textColor)
        LiturgyLine("R.", r, textColor)
    }
}

// ── Static texts ──────────────────────────────────────────────────────────────

private val suffrageBLines = listOf(
    "Save your people, Lord, and bless your inheritance;" to "Govern and uphold them, now and always.",
    "Day by day we bless you;" to "We praise your name for ever.",
    "Lord, keep us from all sin today;" to "Have mercy upon us, Lord, have mercy.",
    "Lord, show us your love and mercy;" to "For we put our trust in you.",
    "In you, Lord, is our hope;" to "And we shall never hope in vain."
)

private val matinsPrayerForMission = "Almighty and everlasting God, by whose Spirit the whole body of your faithful people is governed and sanctified: Receive our supplications and prayers which we offer before you for all members of your holy Church, that in their vocation and ministry they may truly and devoutly serve you; through our Lord and Savior Jesus Christ. Amen."
