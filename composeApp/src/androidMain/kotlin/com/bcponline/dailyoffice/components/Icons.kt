package com.bcponline.dailyoffice.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.R
import kotlinx.datetime.LocalDate

@Composable
actual fun DateTabContent(date: LocalDate) {
    // Android shows just the Icon to save space in the TabRow
    Icon(
        painter = painterResource(R.drawable.date_picker),
        contentDescription = "Select Date: $date",
        modifier = Modifier.size(24.dp)
    )
}

@Composable
actual fun MatinsTabContent() {
    Icon(
        painter = painterResource(R.drawable.matins),
        contentDescription = "Matins",
        modifier = Modifier.size(24.dp)
    )
}

@Composable
actual fun VespersTabContent() {
    Icon(
        painter = painterResource(R.drawable.vespers),
        contentDescription = "Vespers",
        modifier = Modifier.size(24.dp)
    )
}

@Composable
actual fun ComplineTabContent() {
    Icon(
        painter = painterResource(R.drawable.compline),
        contentDescription = "Compline",
        modifier = Modifier.size(24.dp)
    )
}
