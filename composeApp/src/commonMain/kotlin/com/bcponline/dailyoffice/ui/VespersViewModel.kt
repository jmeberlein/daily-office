package com.bcponline.dailyoffice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcponline.dailyoffice.data.ProperFetcher
import com.bcponline.dailyoffice.data.ProperParser
import com.bcponline.dailyoffice.data.SettingsRepository
import com.bcponline.dailyoffice.model.LiturgicalDay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class VespersViewModel : ViewModel() {
    private val _day = MutableStateFlow<LiturgicalDay?>(null)
    val day: StateFlow<LiturgicalDay?> = _day
    val date: StateFlow<LocalDate> = SelectedDate.date

    val showConfession = SettingsRepository.showConfession
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val showFirstCanticle = SettingsRepository.showFirstCanticle
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val showCreed = SettingsRepository.showCreed
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val showSuffrages = SettingsRepository.showSuffrages
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val showIntercessions = SettingsRepository.showIntercessions
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Tab state — reset on date change
    val phosHilaronTab = MutableStateFlow(0)
    val firstCanticleTab = MutableStateFlow(0)
    val secondCanticleTab = MutableStateFlow(0)
    val linkedTab = MutableStateFlow(0) // Advent Sunday only
    val suffragesTab = MutableStateFlow(0)
    val intercessionsTab = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            combine(
                SelectedDate.date,
                SettingsRepository.forceTwoReadings,
                SettingsRepository.useOptionalFeasts,
                SettingsRepository.useExtraFeasts
            ) { date, force, optional, extra -> Pair(date, Triple(force, optional, extra)) }
                .collect { (date, settings) ->
                    val (force, optional, extra) = settings
                    _day.value = null
                    ProperParser.loadFilesForDate(date)
                    _day.value = ProperFetcher.getProperForDate(date, force, optional, extra)
                    phosHilaronTab.value = 0
                    firstCanticleTab.value = 0
                    secondCanticleTab.value = 0
                    linkedTab.value = 0
                    suffragesTab.value = 0
                    intercessionsTab.value = 0
                }
        }
    }
}
