package com.bcponline.dailyoffice

import java.util.prefs.Preferences

actual object Settings {
    private val prefs = Preferences.userRoot().node("com/bcponline/dailyoffice")

    actual var forceTwoReadings: Boolean
        get() = prefs.getBoolean("forceTwoReadings", false)
        set(value) { prefs.putBoolean("forceTwoReadings", value) }

    actual var useOptionalSaints: Boolean
        get() = prefs.getBoolean("useOptionalSaints", false)
        set(value) { prefs.putBoolean("useOptionalSaints", value) }

    actual var useExtraFeasts: Boolean
        get() = prefs.getBoolean("useExtraFeasts", false)
        set(value) { prefs.putBoolean("useExtraFeasts", value) }
}
