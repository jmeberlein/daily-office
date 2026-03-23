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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import dailyoffice.composeapp.generated.resources.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

// Maps a canticle StringResource to its title and scripture reference
private data class CanticleInfo(val res: StringResource, val title: String, val reference: String)

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

@Composable
fun MatinsScreen(vm: MatinsViewModel = viewModel { MatinsViewModel() }) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    var forceTwoReadings by remember { mutableStateOf(false) }
    var useOptionalFeasts by remember { mutableStateOf(true) }
    var useExtraFeasts by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            currentDate = (state as? MatinsUiState.Success)?.date ?: today,
            onDismiss = { showDatePicker = false },
            onConfirm = { date ->
                showDatePicker = false
                vm.load(date, forceTwoReadings, useOptionalFeasts, useExtraFeasts)
            }
        )
    }

    if (showSettings) {
        SettingsDialog(
            forceTwoReadings = forceTwoReadings,
            useOptionalFeasts = useOptionalFeasts,
            useExtraFeasts = useExtraFeasts,
            onDismiss = { showSettings = false },
            onConfirm = { f, o, e ->
                forceTwoReadings = f; useOptionalFeasts = o; useExtraFeasts = e
                showSettings = false
                (state as? MatinsUiState.Success)?.date?.let {
                    vm.load(it, f, o, e)
                }
            }
        )
    }

    when (val s = state) {
        is MatinsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is MatinsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text("Failed to load: ${s.message}")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.load(today, forceTwoReadings, useOptionalFeasts, useExtraFeasts) }) { Text("Retry") }
            }
        }
        is MatinsUiState.Success -> MatinsContent(
            office = s.day.morning,
            date = s.date,
            onPickDate = { showDatePicker = true },
            onSettings = { showSettings = true }
        )
    }
}

@Composable
private fun SettingsDialog(
    forceTwoReadings: Boolean,
    useOptionalFeasts: Boolean,
    useExtraFeasts: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Boolean, Boolean, Boolean) -> Unit
) {
    var f by remember { mutableStateOf(forceTwoReadings) }
    var o by remember { mutableStateOf(useOptionalFeasts) }
    var e by remember { mutableStateOf(useExtraFeasts) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column {
                LabeledSwitch("Force two readings", f) { f = it }
                LabeledSwitch("Optional feasts", o) { o = it }
                LabeledSwitch("Extra feasts", e) { e = it }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(f, o, e) }) { Text("Apply") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun LabeledSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    currentDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.toEpochDays() * 86_400_000L
    )
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { millis ->
                    val days = (millis / 86_400_000L).toInt()
                    onConfirm(LocalDate.fromEpochDays(days))
                }
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = state)
    }
}

@Composable
private fun MatinsContent(office: Office, date: LocalDate, onPickDate: () -> Unit, onSettings: () -> Unit) {
    val color = office.color
    // 'background' in LiturgicalColor is the main liturgical color used as a tint
    val bg      = if (color == LiturgicalColor.NONE) MaterialTheme.colorScheme.background else color.background
    val primary = if (color == LiturgicalColor.NONE) MaterialTheme.colorScheme.primary    else color.primary
    val onBg    = if (color == LiturgicalColor.NONE) MaterialTheme.colorScheme.onBackground else color.onBackground

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = bg, onBackground = onBg,
            primary = primary, surface = bg, onSurface = onBg
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Title row with date picker button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = office.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (office.season != Season.NONE) {
                        Text(
                            text = office.season.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge,
                            color = primary.copy(alpha = 0.7f)
                        )
                    }
                }
                OutlinedButton(onClick = onPickDate) {
                    Text("${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}, ${date.year}")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onSettings) { Text("⚙") }
            }

            Spacer(Modifier.height(24.dp))

            // ── Invitatory ──────────────────────────────────────────────
            SectionHeader("Invitatory and Psalter", primary, onBg)
            Spacer(Modifier.height(8.dp))

            LiturgyLine("Officiant", "Lord, open our lips.", onBg)
            LiturgyLine("People", "And our mouth shall proclaim your praise.", onBg)
            LiturgyLine("All", "Glory to the Father, and to the Son, and to the Holy Spirit: as it was in the beginning, is now, and will be for ever. Amen.", onBg)
            if (office.season != Season.LENT) {
                Rubric("Alleluia.", onBg)
            }

            Spacer(Modifier.height(16.dp))
            CanticleBlock(openingCanticleFor(office), primary, onBg)

            Spacer(Modifier.height(16.dp))

            // Appointed psalms
            SubSectionHeader("Appointed Psalms: ${office.psalter.ifBlank { "—" }}", primary)
            Spacer(Modifier.height(4.dp))
            Rubric("Glory to the Father, and to the Son, and to the Holy Spirit: *\n    as it was in the beginning, is now, and will be for ever. Amen.", onBg)

            Spacer(Modifier.height(24.dp))

            // ── The Lessons ─────────────────────────────────────────────
            SectionHeader("The Lessons", primary, onBg)
            Spacer(Modifier.height(8.dp))

            val hasFirstReading = office.firstReading.isNotBlank()
            val hasSecondReading = office.secondReading.isNotBlank()

            if (hasFirstReading) {
                ReadingLabel("First Reading", office.firstReading, onBg)
                Spacer(Modifier.height(12.dp))
                CanticleBlock(firstCanticleFor(office, date), primary, onBg)
                Spacer(Modifier.height(12.dp))
            }

            if (hasSecondReading) {
                ReadingLabel(if (hasFirstReading) "Second Reading" else "The Reading", office.secondReading, onBg)
                Spacer(Modifier.height(12.dp))
            }

            CanticleBlock(listOf(BENEDICTUS), primary, onBg)

            Spacer(Modifier.height(16.dp))

            SubSectionHeader("The Apostles' Creed", primary)
            Spacer(Modifier.height(8.dp))
            BodyText(apostlesCreed, onBg)

            Spacer(Modifier.height(24.dp))

            // ── The Prayers ──────────────────────────────────────────────
            SectionHeader("The Prayers", primary, onBg)
            Spacer(Modifier.height(8.dp))

            LiturgyLine("Officiant", "The Lord be with you.", onBg)
            LiturgyLine("People", "And also with you.", onBg)
            LiturgyLine("Officiant", "Let us pray.", onBg)

            Spacer(Modifier.height(12.dp))
            BodyText(ourFather, onBg)

            Spacer(Modifier.height(16.dp))
            SubSectionHeader("Suffrages", primary)
            Spacer(Modifier.height(8.dp))
            suffrageBLines.forEach { (v, r) ->
                LiturgyLine("V.", v, onBg)
                LiturgyLine("R.", r, onBg)
            }

            Spacer(Modifier.height(16.dp))
            SubSectionHeader("Collect", primary)
            Spacer(Modifier.height(8.dp))
            if (office.collect.isNotBlank()) BodyText(office.collect, onBg)

            Spacer(Modifier.height(16.dp))
            SubSectionHeader("Intercessions", primary)
            Spacer(Modifier.height(8.dp))
            IntercessionsBlock(onBg, primary)

            Spacer(Modifier.height(24.dp))

            // ── Closing ──────────────────────────────────────────────────
            LiturgyLine("Officiant", "Let us bless the Lord.", onBg)
            LiturgyLine("People", if (office.season == Season.EASTER) "Thanks be to God. Alleluia, alleluia." else "Thanks be to God.", onBg)

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
        office.season == Season.ADVENT && date.dayOfWeek == DayOfWeek.SUNDAY ->
            listOf(THIRD_ISAIAH, WILDERNESS)
        office.season == Season.CHRISTMAS && date.dayOfWeek == DayOfWeek.SUNDAY ->
            listOf(HANNAH, TE_DEUM)
        office.season == Season.LENT && date.dayOfWeek == DayOfWeek.SUNDAY ->
            listOf(HOSEA, KYRIE)
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

// ── UI building blocks ────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(text: String, color: Color, onBg: Color) {
    HorizontalDivider(color = color.copy(alpha = 0.3f))
    Spacer(Modifier.height(8.dp))
    Text(text, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
    HorizontalDivider(color = color.copy(alpha = 0.3f))
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SubSectionHeader(text: String, color: Color) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun LiturgyLine(role: String, text: String, textColor: Color) {
    Text(
        buildAnnotatedString {
            pushStyle(SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.SemiBold))
            append("$role  ")
            pop()
            append(text)
        },
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun Rubric(text: String, textColor: Color) {
    Text(
        text = renderPoetry(text),
        color = textColor.copy(alpha = 0.75f),
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun BodyText(text: String, textColor: Color) {
    Text(
        text = renderPoetry(text),
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
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

/** Renders one or two canticles. If two, shows them in a tab row. */
@Composable
private fun CanticleBlock(options: List<CanticleInfo>, primary: Color, textColor: Color) {
    var selected by remember(options) { mutableIntStateOf(0) }
    if (options.size > 1) {
        TabRow(
            selectedTabIndex = selected,
            containerColor = Color.Transparent,
            contentColor = primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { i, info ->
                Tab(
                    selected = selected == i,
                    onClick = { selected = i },
                    text = { Text(info.title, style = MaterialTheme.typography.labelMedium) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
    val info = options[selected]
    if (options.size == 1) {
        // Single canticle: show title inline
        Text(
            buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                append(info.title)
                if (info.reference.isNotBlank()) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Normal, fontStyle = FontStyle.Italic))
                    append("  ${info.reference}")
                    pop()
                }
            },
            color = primary,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(4.dp))
    } else if (info.reference.isNotBlank()) {
        Text(info.reference, color = primary.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall, fontStyle = FontStyle.Italic)
        Spacer(Modifier.height(4.dp))
    }
    Text(
        text = renderPoetry(stringResource(info.res)),
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp
    )
}

@Composable
private fun IntercessionsBlock(textColor: Color, primary: Color) {
    val tabs = listOf("Form III", "Form IV", "Prayer for Mission")
    var selected by remember { mutableIntStateOf(0) }
    TabRow(
        selectedTabIndex = selected,
        containerColor = Color.Transparent,
        contentColor = primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { i, title ->
            Tab(selected == i, onClick = { selected = i },
                text = { Text(title, style = MaterialTheme.typography.labelMedium) })
        }
    }
    Spacer(Modifier.height(8.dp))
    when (selected) {
        0 -> intercessionsFormIII.forEach { (leader, response) ->
            if (response.isBlank()) {
                BodyText(leader, textColor)
            } else {
                Text(leader, color = textColor, style = MaterialTheme.typography.bodyMedium)
                Text(response, color = textColor, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
            }
            Spacer(Modifier.height(4.dp))
        }
        1 -> intercessionsFormIV.forEach { (leader, response) ->
            if (leader == "Silence") {
                Text("Silence", color = textColor.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
            } else {
                Text(leader, color = textColor, style = MaterialTheme.typography.bodyMedium)
                if (response.isNotBlank()) {
                    Text(response, color = textColor, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        2 -> BodyText(prayerForMission, textColor)
    }
}

// ── Text rendering ────────────────────────────────────────────────────────────

private fun renderPoetry(raw: String): String = raw
    .replace("\\*", "*")
    .replace("\u00A0", " ")
    .trim()

// ── Static liturgical texts ───────────────────────────────────────────────────

private val apostlesCreed = """I believe in God, the Father almighty,
    creator of heaven and earth;
I believe in Jesus Christ, his only Son, our Lord.
    He was conceived by the power of the Holy Spirit
        and born of the Virgin Mary.
    He suffered under Pontius Pilate,
        was crucified, died, and was buried.
    He descended to the dead.
    On the third day he rose again.
    He ascended into heaven,
        and is seated at the right hand of the Father.
    He will come again to judge the living and the dead.
I believe in the Holy Spirit,
    the holy catholic Church,
    the communion of saints,
    the forgiveness of sins,
    the resurrection of the body,
    and the life everlasting. Amen."""

private val ourFather = """Our Father, who art in heaven,
     hallowed be thy Name,
     thy kingdom come,
     thy will be done,
         on earth as it is in heaven.
Give us this day our daily bread.
And forgive us our trespasses,
     as we forgive those
         who trespass against us.
And lead us not into temptation,
     but deliver us from evil.
For thine is the kingdom,
     and the power, and the glory,
     for ever and ever. Amen."""

private val suffrageBLines = listOf(
    "Save your people, Lord, and bless your inheritance;" to "Govern and uphold them, now and always.",
    "Day by day we bless you;" to "We praise your name for ever.",
    "Lord, keep us from all sin today;" to "Have mercy upon us, Lord, have mercy.",
    "Lord, show us your love and mercy;" to "For we put our trust in you.",
    "In you, Lord, is our hope;" to "And we shall never hope in vain."
)

// Pairs of (leader line, people response). Empty response = said by all.
private val intercessionsFormIII = listOf(
    "Father, we pray for your holy catholic Church;" to "That we all may be one.",
    "Grant that every member of the Church may truly and humbly serve you;" to "That your Name may be glorified by all people.",
    "We pray for all bishops, priests, and deacons;" to "That they may be faithful ministers of your Word and Sacraments.",
    "We pray for all who govern and hold authority in the nations of the world;" to "That there may be justice and peace on the earth.",
    "Give us grace to do your will in all that we undertake;" to "That our works may find favor in your sight.",
    "Have compassion on those who suffer from any grief or trouble;" to "That they may be delivered from their distress.",
    "Give to the departed eternal rest." to "Let light perpetual shine upon them.",
    "We praise you for your saints who have entered into joy;" to "May we also come to share in your heavenly kingdom.",
    "Lord, hear the prayers of thy people; and what we have asked faithfully, grant that we may obtain effectually, to the glory of thy Name; through Jesus Christ our Lord. Amen." to ""
)

// "Silence" is a special marker rendered differently
private val intercessionsFormIV = listOf(
    "Let us pray for the Church and for the world." to "",
    "Grant, Almighty God, that all who confess your Name may be united in your truth, live together in your love, and reveal your glory in the world." to "",
    "Silence" to "",
    "Lord, in your mercy" to "Hear our prayer.",
    "Guide the people of this land, and of all the nations, in the ways of justice and peace; that we may honor one another and serve the common good." to "",
    "Silence" to "",
    "Lord, in your mercy" to "Hear our prayer.",
    "Give us all a reverence for the earth as your own creation, that we may use its resources rightly in the service of others and to your honor and glory." to "",
    "Silence" to "",
    "Lord, in your mercy" to "Hear our prayer.",
    "Bless all whose lives are closely linked with ours, and grant that we may serve Christ in them, and love one another as he loves us." to "",
    "Silence" to "",
    "Lord, in your mercy" to "Hear our prayer.",
    "Comfort and heal all those who suffer in body, mind, or spirit; give them courage and hope in their troubles, and bring them the joy of your salvation." to "",
    "Silence" to "",
    "Lord, in your mercy" to "Hear our prayer.",
    "We commend to your mercy all who have died, that your will for them may be fulfilled; and we pray that we may share with all your saints in your eternal kingdom." to "",
    "Silence" to "",
    "Lord, in your mercy" to "Hear our prayer.",
    "Lord, hear the prayers of thy people; and what we have asked faithfully, grant that we may obtain effectually, to the glory of thy Name; through Jesus Christ our Lord. Amen." to ""
)

private val prayerForMission = "Almighty and everlasting God, by whose Spirit the whole body of your faithful people is governed and sanctified: Receive our supplications and prayers which we offer before you for all members of your holy Church, that in their vocation and ministry they may truly and devoutly serve you; through our Lord and Savior Jesus Christ. Amen."
