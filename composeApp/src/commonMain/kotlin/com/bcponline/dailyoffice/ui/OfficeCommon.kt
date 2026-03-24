package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.Office
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
// ── Canticle metadata ─────────────────────────────────────────────────────────

data class CanticleInfo(val res: StringResource, val title: String, val reference: String)

// ── Theme helpers ─────────────────────────────────────────────────────────────

data class OfficeColors(val bg: Color, val primary: Color, val onBg: Color)

@Composable
fun officeColorsFor(office: Office): OfficeColors {
    val color = office.color
    return if (color == LiturgicalColor.NONE) {
        OfficeColors(
            bg = MaterialTheme.colorScheme.background,
            primary = MaterialTheme.colorScheme.primary,
            onBg = MaterialTheme.colorScheme.onBackground
        )
    } else {
        OfficeColors(bg = color.background, primary = color.primary, onBg = color.onBackground)
    }
}

// ── Date picker ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficeDatePickerDialog(
    currentDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.toEpochDays() * 86_400_000L
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { millis ->
                    onConfirm(LocalDate.fromEpochDays((millis / 86_400_000L).toInt()))
                }
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = state)
    }
}

// ── Settings dialog ───────────────────────────────────────────────────────────

@Composable
fun OfficeSettingsDialog(
    current: OfficeSettings,
    onDismiss: () -> Unit,
    onConfirm: (OfficeSettings) -> Unit
) {
    var s by remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column {
                LabeledSwitch("Condensed version", s.condensed) { s = s.copy(condensed = it) }
                LabeledSwitch("Force two readings", s.forceTwoReadings) { s = s.copy(forceTwoReadings = it) }
                LabeledSwitch("Optional feasts", s.useOptionalFeasts) { s = s.copy(useOptionalFeasts = it) }
                LabeledSwitch("Extra feasts", s.useExtraFeasts) { s = s.copy(useExtraFeasts = it) }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(s) }) { Text("Apply") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun LabeledSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// ── Title row ─────────────────────────────────────────────────────────────────

@Composable
fun OfficeTitleRow(
    title: String,
    subtitle: String,
    date: LocalDate,
    primary: Color,
    onPickDate: () -> Unit,
    onSettings: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineMedium, color = primary, fontWeight = FontWeight.Bold)
            if (subtitle.isNotBlank()) {
                Text(subtitle, style = MaterialTheme.typography.labelLarge, color = primary.copy(alpha = 0.7f))
            }
        }
        OutlinedButton(onClick = onPickDate) {
            Text("${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}, ${date.year}")
        }
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = onSettings) { Text("⚙") }
    }
}

// ── Shared composable primitives ──────────────────────────────────────────────

@Composable
fun OfficeSectionHeader(text: String, color: Color) {
    HorizontalDivider(color = color.copy(alpha = 0.3f))
    Spacer(Modifier.height(8.dp))
    Text(text, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
    HorizontalDivider(color = color.copy(alpha = 0.3f))
    Spacer(Modifier.height(8.dp))
}

@Composable
fun OfficeSubHeader(text: String, color: Color) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.SemiBold)
}

@Composable
fun LiturgyLine(role: String, text: String, textColor: Color) {
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
fun OfficeRubric(text: String, textColor: Color) {
    Text(
        text = renderPoetry(text),
        color = textColor.copy(alpha = 0.75f),
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
fun OfficeBodyText(text: String, textColor: Color) {
    Text(
        text = renderPoetry(text),
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

/** Renders one or two canticles. If two, shows them in a tab row. */
@Composable
fun CanticleBlock(options: List<CanticleInfo>, primary: Color, textColor: Color) {
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

// ── Shared static liturgical texts ───────────────────────────────────────────

val apostlesCreed = """I believe in God, the Father almighty,
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

val ourFather = """Our Father, who art in heaven,
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

val suffrageALines = listOf(
    "Show us your mercy, O Lord;" to "And grant us your salvation.",
    "Clothe your ministers with righteousness;" to "Let your people sing with joy.",
    "Give peace, O Lord, in all the world;" to "For only in you can we live in safety.",
    "Lord, keep this nation under your care;" to "And guide us in the way of justice and truth.",
    "Let your way be known upon earth;" to "Your saving health among all nations.",
    "Let not the needy, O Lord, be forgotten;" to "Nor the hope of the poor be taken away.",
    "Create in us clean hearts, O God;" to "And sustain us with your Holy Spirit."
)

val intercessionsFormIII = listOf(
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

val intercessionsFormIV = listOf(
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

/** Renders Form III / Form IV / Prayer for Mission in a tab row. [prayerForMission] is office-specific. */
@Composable
fun IntercessionsBlock(textColor: Color, primary: Color, prayerForMission: String) {
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
            if (response.isBlank()) OfficeBodyText(leader, textColor)
            else {
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
                if (response.isNotBlank()) Text(response, color = textColor, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
            }
            Spacer(Modifier.height(4.dp))
        }
        2 -> OfficeBodyText(prayerForMission, textColor)
    }
}

fun renderPoetry(raw: String): String = raw
    .replace("\\*", "*")
    .replace("\u00A0", " ")
    .trim()

// ── Themed office wrapper ─────────────────────────────────────────────────────

@Composable
fun OfficeTheme(colors: OfficeColors, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = colors.bg, onBackground = colors.onBg,
            primary = colors.primary, surface = colors.bg, onSurface = colors.onBg
        ),
        content = content
    )
}
