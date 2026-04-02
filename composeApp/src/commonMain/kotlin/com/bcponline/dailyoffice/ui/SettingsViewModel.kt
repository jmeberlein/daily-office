package com.bcponline.dailyoffice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcponline.dailyoffice.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel : ViewModel() {
    val forceTwoReadings = SettingsRepository.forceTwoReadings
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val useOptionalFeasts = SettingsRepository.useOptionalFeasts
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val useExtraFeasts = SettingsRepository.useExtraFeasts
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val showFirstCanticle = SettingsRepository.showFirstCanticle
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun setForceTwoReadings(v: Boolean) = SettingsRepository.setForceTwoReadings(v)
    fun setUseOptionalFeasts(v: Boolean) = SettingsRepository.setUseOptionalFeasts(v)
    fun setUseExtraFeasts(v: Boolean) = SettingsRepository.setUseExtraFeasts(v)
    fun setShowFirstCanticle(v: Boolean) = SettingsRepository.setShowFirstCanticle(v)
}
