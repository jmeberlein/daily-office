package com.bcponline.dailyoffice.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import kotlinx.coroutines.flow.Flow

object SettingsRepository {
    private lateinit var settings: ObservableSettings

    fun init(settings: ObservableSettings) {
        this.settings = settings
    }

    val forceTwoReadings: Flow<Boolean> get() = settings.getBooleanFlow(KEY_FORCE_TWO_READINGS, true)
    val useOptionalFeasts: Flow<Boolean> get() = settings.getBooleanFlow(KEY_USE_OPTIONAL_FEASTS, false)
    val useExtraFeasts: Flow<Boolean> get() = settings.getBooleanFlow(KEY_USE_EXTRA_FEASTS, false)
    val showFirstCanticle: Flow<Boolean> get() = settings.getBooleanFlow(KEY_SHOW_FIRST_CANTICLE, true)
    val showCreed: Flow<Boolean> get() = settings.getBooleanFlow(KEY_SHOW_CREED, true)
    val showSuffrages: Flow<Boolean> get() = settings.getBooleanFlow(KEY_SHOW_SUFFRAGES, true)
    val showIntercessions: Flow<Boolean> get() = settings.getBooleanFlow(KEY_SHOW_INTERCESSIONS, false)

    fun setForceTwoReadings(value: Boolean) = settings.putBoolean(KEY_FORCE_TWO_READINGS, value)
    fun setUseOptionalFeasts(value: Boolean) {
        settings.putBoolean(KEY_USE_OPTIONAL_FEASTS, value)
        if (!value) settings.putBoolean(KEY_USE_EXTRA_FEASTS, false)
    }
    fun setUseExtraFeasts(value: Boolean) = settings.putBoolean(KEY_USE_EXTRA_FEASTS, value)
    fun setShowFirstCanticle(value: Boolean) = settings.putBoolean(KEY_SHOW_FIRST_CANTICLE, value)
    fun setShowCreed(value: Boolean) = settings.putBoolean(KEY_SHOW_CREED, value)
    fun setShowSuffrages(value: Boolean) = settings.putBoolean(KEY_SHOW_SUFFRAGES, value)
    fun setShowIntercessions(value: Boolean) = settings.putBoolean(KEY_SHOW_INTERCESSIONS, value)

    private const val KEY_FORCE_TWO_READINGS = "force_two_readings"
    private const val KEY_USE_OPTIONAL_FEASTS = "use_optional_feasts"
    private const val KEY_USE_EXTRA_FEASTS = "use_extra_feasts"
    private const val KEY_SHOW_FIRST_CANTICLE = "show_first_canticle"
    private const val KEY_SHOW_CREED = "show_creed"
    private const val KEY_SHOW_SUFFRAGES = "show_suffrages"
    private const val KEY_SHOW_INTERCESSIONS = "show_intercessions"
}
