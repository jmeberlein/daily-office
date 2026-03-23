package com.bcponline.dailyoffice

import com.bcponline.dailyoffice.data.SettingsRepository
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual fun initSettings() {
    SettingsRepository.init(PreferencesSettings(Preferences.userRoot().node("dailyoffice")))
}
