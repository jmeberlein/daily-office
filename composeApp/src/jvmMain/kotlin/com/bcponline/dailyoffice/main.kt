package com.bcponline.dailyoffice

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    initSettings()
    Window(
        onCloseRequest = ::exitApplication,
        title = "DailyOffice",
    ) {
        App()
    }
}