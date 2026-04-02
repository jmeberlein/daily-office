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

class OverviewViewModel : ViewModel() {
    private val _day = MutableStateFlow<LiturgicalDay?>(null)
    val day: StateFlow<LiturgicalDay?> = _day
    val date: StateFlow<LocalDate> = SelectedDate.date

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
                }
        }
    }
}
