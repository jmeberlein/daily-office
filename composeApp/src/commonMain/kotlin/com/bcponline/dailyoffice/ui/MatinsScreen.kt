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
import com.bcponline.dailyoffice.data.MatinsCanticleSelector
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/** Converts the XML canticle format to plain displayable text. */
private fun String.formatCanticle(): String =
    this.replace("\\*", "*")
        .replace("&#160;", "\u00A0")
        .trim()

@Composable
fun MatinsScreen(vm: MatinsViewModel = viewModel { MatinsViewModel() }) {
    val day by vm.day.collectAsStateWithLifecycle()
    val showFirstCanticle by vm.showFirstCanticle.collectAsStateWithLifecycle()
    val showCreed by vm.showCreed.collectAsStateWithLifecycle()
    val showSuffrages by vm.showSuffrages.collectAsStateWithLifecycle()
    val showIntercessions by vm.showIntercessions.collectAsStateWithLifecycle()
    val intercessionsTab by vm.intercessionsTab.collectAsStateWithLifecycle()
    val firstCanticleTab by vm.firstCanticleTab.collectAsStateWithLifecycle()
    val suffragesTab by vm.suffragesTab.collectAsStateWithLifecycle()

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
                CanticleBlock(MatinsCanticleSelector.invitatoryCanticle(office))
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
                CanticleBlock(MatinsCanticleSelector.BENEDICTUS)
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
                    SuffragesBlock(suffragesTab, ServiceTexts.SUFFRAGES_B_MATINS) { vm.suffragesTab.value = it }
                }
                if (office.collect.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    LabeledText("Collect", office.collect)
                }
                if (showIntercessions) {
                    Spacer(Modifier.height(8.dp))
                    IntercessionsBlock(intercessionsTab, ServiceTexts.PRAYER_FOR_MISSION_MATINS) { vm.intercessionsTab.value = it }
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
private fun CanticleBlock(canticle: Canticle) {
    val text = stringResource(canticle.resource).formatCanticle()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(canticle.name, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary)
        if (canticle.reference.isNotBlank())
            Text(canticle.reference, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        OfficeText(text)
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
