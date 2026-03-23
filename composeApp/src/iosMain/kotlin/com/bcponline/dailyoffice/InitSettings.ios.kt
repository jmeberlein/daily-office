package com.bcponline.dailyoffice

import com.bcponline.dailyoffice.data.SettingsRepository
import com.russhwolf.settings.NSUserDefaultsSettings

actual fun initSettings() {
    SettingsRepository.init(NSUserDefaultsSettings.Factory().create("dailyoffice"))
}
