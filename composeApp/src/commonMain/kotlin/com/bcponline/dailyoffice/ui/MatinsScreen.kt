package com.bcponline.dailyoffice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcponline.dailyoffice.data.MatinsCanticleSelector
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val LORD_S_PRAYER = """
Our Father, who art in heaven,
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
     for ever and ever. Amen.
""".trim()

/** Converts the XML canticle format to plain displayable text. */
private fun String.formatCanticle(): String =
    this.replace("\\*", "*")
        .replace("&#160;", "\u00A0")
        .trim()

@Composable
fun MatinsScreen(vm: MatinsViewModel = viewModel { MatinsViewModel() }) {
    val day by vm.day.collectAsStateWithLifecycle()
    val showFirstCanticle by vm.showFirstCanticle.collectAsStateWithLifecycle()
    val firstCanticleTab by vm.firstCanticleTab.collectAsStateWithLifecycle()

    val office = day?.morning
    val color = office?.color ?: LiturgicalColor.NONE

    MaterialTheme(
        colorScheme = if (color != LiturgicalColor.NONE)
            MaterialTheme.colorScheme.copy(
                background = color.background,
                surface = color.background,
                primary = color.primary,
                onBackground = color.onBackground,
                onSurface = color.onBackground,
                onSurfaceVariant = color.onBackground
            )
        else MaterialTheme.colorScheme
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column {
                Text(
                    office?.name ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Morning Prayer", style = MaterialTheme.typography.headlineMedium)
            }

            if (office == null) {
                CircularProgressIndicator()
                return@Column
            }

            // Invitatory & Psalter
            OfficeSection("Invitatory and Psalter") {
                val alleluia = if (office.season != Season.LENT) " Alleluia." else ""
                OfficeText("""
Officiant: Lord, open our lips.
People: And our mouth shall proclaim your praise.
All: Glory to the Father, and to the Son, and to the Holy Spirit: as it was in the beginning, is now, and will be for ever. Amen.$alleluia
                """.trimIndent())
                Spacer(Modifier.height(8.dp))
                CanticleBlock(
                    title = if (office.season == Season.EASTER) "Pascha Nostrum" else "Psalm 95",
                    resource = MatinsCanticleSelector.invitatoryResource(office)
                )
                if (office.psalter.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    LabeledText("Psalms", office.psalter)
                }
            }

            // Readings
            val hasTwoReadings = office.firstReading.isNotBlank()
            OfficeSection("Readings") {
                if (hasTwoReadings) {
                    LabeledText("First Reading", office.firstReading)
                    if (showFirstCanticle) {
                        Spacer(Modifier.height(8.dp))
                        val options = MatinsCanticleSelector.firstCanticleOptions(office, vm.date.collectAsStateWithLifecycle().value)
                        CanticleChoiceBlock(options, firstCanticleTab) { vm.firstCanticleTab.value = it }
                    }
                    Spacer(Modifier.height(8.dp))
                    LabeledText("Second Reading", office.secondReading)
                } else {
                    LabeledText("Reading", office.secondReading)
                }
                Spacer(Modifier.height(8.dp))
                CanticleBlock("Benedictus", com.bcponline.dailyoffice.data.CanticleRepository.BENEDICTUS)
            }

            // Prayers
            OfficeSection("Prayers") {
                OfficeText(LORD_S_PRAYER)
                if (office.collect.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    LabeledText("Collect", office.collect)
                }
            }
        }
        } // CompositionLocalProvider
    }
}

@Composable
private fun OfficeSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary)
        HorizontalDivider()
        content()
    }
}

@Composable
private fun LabeledText(label: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary)
        OfficeText(text)
    }
}

@Composable
private fun OfficeText(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun CanticleBlock(title: String, resource: StringResource) {
    val text = stringResource(resource).formatCanticle()
    LabeledText(title, text)
}

@Composable
private fun CanticleChoiceBlock(
    options: List<Pair<String, StringResource>>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    if (options.size == 1) {
        CanticleBlock(options[0].first, options[0].second)
        return
    }
    Column {
        TabRow(selectedTabIndex = selectedTab) {
            options.forEachIndexed { i, (name, _) ->
                Tab(selected = selectedTab == i, onClick = { onTabSelected(i) },
                    text = { Text(name, style = MaterialTheme.typography.labelMedium) })
            }
        }
        Spacer(Modifier.height(8.dp))
        val (_, resource) = options[selectedTab]
        OfficeText(stringResource(resource).formatCanticle())
    }
}
