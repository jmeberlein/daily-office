package com.bcponline.dailyoffice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcponline.dailyoffice.data.FileRegistry
import com.bcponline.dailyoffice.data.ProperFetcher
import com.bcponline.dailyoffice.data.ProperParser
import com.bcponline.dailyoffice.model.LiturgicalDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

sealed interface MatinsUiState {
    data object Loading : MatinsUiState
    data class Success(val day: LiturgicalDay, val date: LocalDate) : MatinsUiState
    data class Error(val message: String) : MatinsUiState
}

class MatinsViewModel : ViewModel() {
    private val _state = MutableStateFlow<MatinsUiState>(MatinsUiState.Loading)
    val state: StateFlow<MatinsUiState> = _state

    init {
        load(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }

    fun load(date: LocalDate, forceTwoReadings: Boolean = false, useOptionalFeasts: Boolean = true, useExtraFeasts: Boolean = false) {
        viewModelScope.launch {
            _state.value = MatinsUiState.Loading
            try {
                ProperParser.loadFilesForDate(date)
                FileRegistry.loadFiles("daily_propers", "optional_feasts")
                val day = ProperFetcher.getProperForDate(
                    date,
                    forceTwoReadings = forceTwoReadings,
                    useOptionalFeasts = useOptionalFeasts,
                    useExtraFeasts = useExtraFeasts
                )
                _state.value = MatinsUiState.Success(day, date)
            } catch (e: Exception) {
                _state.value = MatinsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
