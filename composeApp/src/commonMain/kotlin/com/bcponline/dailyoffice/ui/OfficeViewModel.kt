package com.bcponline.dailyoffice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcponline.dailyoffice.data.FileRegistry
import com.bcponline.dailyoffice.data.ProperFetcher
import com.bcponline.dailyoffice.data.ProperParser
import com.bcponline.dailyoffice.data.SettingsRepository
import com.bcponline.dailyoffice.model.LiturgicalDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock

sealed interface OfficeUiState {
    data object Loading : OfficeUiState
    data class Success(val day: LiturgicalDay, val date: LocalDate) : OfficeUiState
    data class Error(val message: String) : OfficeUiState
}

class OfficeViewModel : ViewModel() {
    private val _state = MutableStateFlow<OfficeUiState>(OfficeUiState.Loading)
    val state: StateFlow<OfficeUiState> = _state

    private val _settings = MutableStateFlow(SettingsRepository.load())
    val settings: StateFlow<OfficeSettings> = _settings

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val date = if (now.hour < 4)
            Clock.System.now().minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()).date
        else
            now.date
        load(date)
    }

    fun load(date: LocalDate, settings: OfficeSettings = _settings.value) {
        _settings.value = settings
        SettingsRepository.save(settings)
        viewModelScope.launch {
            _state.value = OfficeUiState.Loading
            try {
                ProperParser.loadFilesForDate(date)
                FileRegistry.loadFiles("daily_propers", "optional_feasts", "extra_feasts")
                val day = ProperFetcher.getProperForDate(
                    date,
                    forceTwoReadings = settings.forceTwoReadings,
                    useOptionalFeasts = settings.useOptionalFeasts,
                    useExtraFeasts = settings.useExtraFeasts
                )
                _state.value = OfficeUiState.Success(day, date)
            } catch (e: Exception) {
                _state.value = OfficeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
