package com.bcponline.dailyoffice.components

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
expect fun DateTabContent(date: LocalDate)

@Composable
expect fun MatinsTabContent()

@Composable
expect fun VespersTabContent()

@Composable
expect fun ComplineTabContent()
