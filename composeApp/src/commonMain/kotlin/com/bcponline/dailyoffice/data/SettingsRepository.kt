package com.bcponline.dailyoffice.data

import com.bcponline.dailyoffice.ui.OfficeSettings
import com.russhwolf.settings.Settings

object SettingsRepository {
    private lateinit var settings: Settings

    fun init(settings: Settings) {
        this.settings = settings
    }

    fun load(): OfficeSettings = OfficeSettings(
        forceTwoReadings = settings.getBoolean("forceTwoReadings", false),
        useOptionalFeasts = settings.getBoolean("useOptionalFeasts", true),
        useExtraFeasts = settings.getBoolean("useExtraFeasts", false),
        condensed = settings.getBoolean("condensed", false)
    )

    fun save(s: OfficeSettings) {
        settings.putBoolean("forceTwoReadings", s.forceTwoReadings)
        settings.putBoolean("useOptionalFeasts", s.useOptionalFeasts)
        settings.putBoolean("useExtraFeasts", s.useExtraFeasts)
        settings.putBoolean("condensed", s.condensed)
    }
}
