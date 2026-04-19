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
import com.bcponline.dailyoffice.data.Canticle
import com.bcponline.dailyoffice.data.VespersCanticleSelector
import com.bcponline.dailyoffice.data.VespersCanticles
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.Season
import org.jetbrains.compose.resources.stringResource

private fun String.formatCanticle(): String =
    this.replace("\\*", "*").replace("&#160;", "\u00A0").trim()

@Composable
fun VespersScreen(vm: VespersViewModel = viewModel { VespersViewModel() }) {
    val day by vm.day.collectAsStateWithLifecycle()
    val showFirstCanticle by vm.showFirstCanticle.collectAsStateWithLifecycle()
    val showCreed by vm.showCreed.collectAsStateWithLifecycle()
    val showSuffrages by vm.showSuffrages.collectAsStateWithLifecycle()
    val showIntercessions by vm.showIntercessions.collectAsStateWithLifecycle()
    val intercessionsTab by vm.intercessionsTab.collectAsStateWithLifecycle()
    val suffragesTab by vm.suffragesTab.collectAsStateWithLifecycle()
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
                            VespersCanticleSelector.PHOS_HILARON,
                            VespersCanticleSelector.PHOS_HILARON_METRICAL
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
                            CanticleBlock(VespersCanticleSelector.MAGNIFICAT)
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
                            LabeledText("First Reading", office.firstReading)
                            Spacer(Modifier.height(8.dp))
                            TabRow(selectedTabIndex = linkedTab) {
                                canticles.pairs.forEachIndexed { i, (first, _) ->
                                    Tab(selected = linkedTab == i, onClick = { vm.linkedTab.value = i },
                                        text = { Text(first.name, style = MaterialTheme.typography.labelMedium) })
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            val (firstCanticle, secondCanticle) = canticles.pairs[linkedTab]
                            if (firstCanticle.reference.isNotBlank())
                                Text(firstCanticle.reference, style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(2.dp))
                            OfficeText(stringResource(firstCanticle.resource).formatCanticle())
                            Spacer(Modifier.height(8.dp))
                            LabeledText("Second Reading", office.secondReading)
                            Spacer(Modifier.height(8.dp))
                            CanticleBlock(secondCanticle)
                        }
                    }
                    if (showCreed) {
                        Spacer(Modifier.height(8.dp))
                        LabeledText("Apostles' Creed", stringResource(ServiceTexts.APOSTLES_CREED))
                    }
                }

                // Prayers
                OfficeSection("Prayers") {
                    OfficeText(stringResource(ServiceTexts.LORDS_PRAYER))
                    if (showSuffrages) {
                        Spacer(Modifier.height(8.dp))
                        SuffragesBlock(suffragesTab, ServiceTexts.SUFFRAGES_B_VESPERS) { vm.suffragesTab.value = it }
                    }
                    if (office.collect.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        LabeledText("Collect", office.collect)
                    }
                    if (showIntercessions) {
                        Spacer(Modifier.height(8.dp))
                        IntercessionsBlock(intercessionsTab, ServiceTexts.PRAYER_FOR_MISSION_VESPERS) { vm.intercessionsTab.value = it }
                    }

                    Spacer(Modifier.height(8.dp))
                    val isEastertide = office.season == Season.EASTER || office.season == Season.ASCENSION
                    val v = if (isEastertide) ServiceTexts.CLOSING_VERSICLE_V_EASTER else ServiceTexts.CLOSING_VERSICLE_V
                    val r = if (isEastertide) ServiceTexts.CLOSING_VERSICLE_R_EASTER else ServiceTexts.CLOSING_VERSICLE_R
                    OfficeText("Officiant: ${stringResource(v)}\nPeople: ${stringResource(r)}")
                }
            }
        }
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
private fun CanticleBlock(canticle: Canticle) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(canticle.name, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary)
        if (canticle.reference.isNotBlank())
            Text(canticle.reference, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        OfficeText(stringResource(canticle.resource).formatCanticle())
    }
}

@Composable
private fun CanticleChoiceBlock(
    options: List<Canticle>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    if (options.size == 1) {
        CanticleBlock(options[0])
        return
    }
    Column {
        TabRow(selectedTabIndex = selectedTab) {
            options.forEachIndexed { i, canticle ->
                Tab(selected = selectedTab == i, onClick = { onTabSelected(i) },
                    text = { Text(canticle.name, style = MaterialTheme.typography.labelMedium) })
            }
        }
        Spacer(Modifier.height(8.dp))
        val canticle = options[selectedTab]
        if (canticle.reference.isNotBlank())
            Text(canticle.reference, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        OfficeText(stringResource(canticle.resource).formatCanticle())
    }
}
