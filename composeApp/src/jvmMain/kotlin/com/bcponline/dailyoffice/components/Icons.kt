package com.bcponline.dailyoffice.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
actual fun DateTabContent(date: LocalDate) {
    // Desktop has more horizontal space, so we show the actual date string
    Text(date.toString())
}

@Composable
actual fun MatinsTabContent() {
    Text("Matins")
}

@Composable
actual fun VespersTabContent() {
    Text("Vespers")
}

@Composable
actual fun ComplineTabContent() {
    Text("Compline")
}