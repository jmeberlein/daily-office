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
import com.bcponline.dailyoffice.data.CanticleRepository
import com.bcponline.dailyoffice.data.VespersCanticleSelector
import com.bcponline.dailyoffice.data.VespersCanticles
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.Season
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private fun String.formatCanticle(): String =
    this.replace("\\*", "*").replace("&#160;", "\u00A0").trim()

@Composable
fun VespersScreen(vm: VespersViewModel = viewModel { VespersViewModel() }) {
    val day by vm.day.collectAsStateWithLifecycle()
    val showFirstCanticle by vm.showFirstCanticle.collectAsStateWithLifecycle()
    val phosTab by vm.phosHilaronTab.collectAsStateWithLifecycle()
    val firstTab by vm.firstCanticleTab.collectAsStateWithLifecycle()
    val secondTab by vm.secondCanticleTab.collectAsStateWithLifecycle()
    val linkedTab by vm.linkedTab.collectAsStateWithLifecycle()
    val date by vm.date.collectAsStateWithLifecycle()

    val office = day?.evening
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
                Column {
                    Text(office?.name ?: "", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Text("Evening Prayer", style = MaterialTheme.typography.headlineMedium)
                }

                if (office == null) {
                    CircularProgressIndicator()
                    return@Column
                }

                // Invitatory & Psalter
                OfficeSection("Invitatory and Psalter") {
                    val alleluia = if (office.season != Season.LENT) " Alleluia." else ""
                    OfficeText("""
Officiant: O God, make speed to save us.
People: O Lord, make haste to help us.
All: Glory to the Father, and to the Son, and to the Holy Spirit: as it was in the beginning, is now, and will be for ever. Amen.$alleluia
                    """.trimIndent())
                    Spacer(Modifier.height(8.dp))
                    CanticleChoiceBlock(
                        options = listOf(
                            "Phos Hilaron" to CanticleRepository.PHOS_HILARON,
                            "Phos Hilaron (Metrical)" to CanticleRepository.PHOS_HILARON_METRICAL
                        ),
                        selectedTab = phosTab,
                        onTabSelected = { vm.phosHilaronTab.value = it }
                    )
                    if (office.psalter.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        LabeledText("Psalms", office.psalter)
                    }
                }

                // Readings & Canticles
                val canticles = VespersCanticleSelector.select(office, date, showFirstCanticle)
                OfficeSection("Readings") {
                    when (canticles) {
                        is VespersCanticles.MagnificatOnly -> {
                            if (office.firstReading.isNotBlank()) {
                                LabeledText("First Reading", office.firstReading)
                                Spacer(Modifier.height(8.dp))
                                LabeledText("Second Reading", office.secondReading)
                            } else {
                                LabeledText("Reading", office.secondReading)
                            }
                            Spacer(Modifier.height(8.dp))
                            CanticleBlock("Magnificat", CanticleRepository.MAGNIFICAT)
                        }
                        is VespersCanticles.Independent -> {
                            LabeledText("First Reading", office.firstReading)
                            Spacer(Modifier.height(8.dp))
                            CanticleChoiceBlock(canticles.first, firstTab) { vm.firstCanticleTab.value = it }
                            Spacer(Modifier.height(8.dp))
                            LabeledText("Second Reading", office.secondReading)
                            Spacer(Modifier.height(8.dp))
                            CanticleChoiceBlock(canticles.second, secondTab) { vm.secondCanticleTab.value = it }
                        }
                        is VespersCanticles.Linked -> {
                            // Advent Sunday: tab 0 = Song of the Spirit + Magnificat,
                            //                tab 1 = Magnificat + Nunc Dimittis
                            LabeledText("First Reading", office.firstReading)
                            Spacer(Modifier.height(8.dp))
                            TabRow(selectedTabIndex = linkedTab) {
                                Tab(selected = linkedTab == 0, onClick = { vm.linkedTab.value = 0 },
                                    text = { Text("Song of the Spirit", style = MaterialTheme.typography.labelMedium) })
                                Tab(selected = linkedTab == 1, onClick = { vm.linkedTab.value = 1 },
                                    text = { Text("Magnificat", style = MaterialTheme.typography.labelMedium) })
                            }
                            Spacer(Modifier.height(8.dp))
                            OfficeText(stringResource(
                                if (linkedTab == 0) CanticleRepository.SPIRIT else CanticleRepository.MAGNIFICAT
                            ).formatCanticle())
                            Spacer(Modifier.height(8.dp))
                            LabeledText("Second Reading", office.secondReading)
                            Spacer(Modifier.height(8.dp))
                            OfficeText(stringResource(
                                if (linkedTab == 0) CanticleRepository.MAGNIFICAT else CanticleRepository.NUNC_DIMITTIS
                            ).formatCanticle())
                        }
                    }
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
        }
    }
}

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
    LabeledText(title, stringResource(resource).formatCanticle())
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
        OfficeText(stringResource(options[selectedTab].second).formatCanticle())
    }
}
