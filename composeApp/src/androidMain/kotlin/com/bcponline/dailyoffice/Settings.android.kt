package com.bcponline.dailyoffice

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

actual object Settings {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("daily_office", Context.MODE_PRIVATE)
    }

    actual var forceTwoReadings: Boolean
        get() = prefs.getBoolean("forceTwoReadings", false)
        set(value) { prefs.edit().putBoolean("forceTwoReadings", value).apply() }

    actual var useOptionalSaints: Boolean
        get() = prefs.getBoolean("useOptionalSaints", false)
        set(value) { prefs.edit().putBoolean("useOptionalSaints", value).apply() }

    actual var useExtraFeasts: Boolean
        get() = prefs.getBoolean("useExtraFeasts", false)
        set(value) { prefs.edit().putBoolean("useExtraFeasts", value).apply() }
}
