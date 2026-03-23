package com.bcponline.dailyoffice

import android.content.Context
import com.bcponline.dailyoffice.data.SettingsRepository
import com.russhwolf.settings.SharedPreferencesSettings

lateinit var appContext: Context

actual fun initSettings() {
    SettingsRepository.init(SharedPreferencesSettings(appContext.getSharedPreferences("dailyoffice", Context.MODE_PRIVATE)))
}
