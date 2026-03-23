package com.bcponline.dailyoffice.ui

data class OfficeSettings(
    val forceTwoReadings: Boolean = false,
    val useOptionalFeasts: Boolean = true,
    val useExtraFeasts: Boolean = false,
    val condensed: Boolean = false
)
