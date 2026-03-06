package com.bcponline.dailyoffice.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.bcponline.dailyoffice.R
import kotlinx.datetime.LocalDate

@Composable
actual fun DateTabContent(date: LocalDate) {
    // Android shows just the Icon to save space in the TabRow
    Icon(
        painter = painterResource(R.drawable.date_picker),
        contentDescription = "Select Date: $date"
    )
}

@Composable
actual fun MatinsTabContent() {
    Icon(
        painter = painterResource(R.drawable.matins),
        contentDescription = "Matins"
    )
}

@Composable
actual fun VespersTabContent() {
    Icon(
        painter = painterResource(R.drawable.vespers),
        contentDescription = "Vespers"
    )
}

@Composable
actual fun ComplineTabContent() {
    Icon(
        painter = painterResource(R.drawable.compline),
        contentDescription = "Compline"
    )
}
