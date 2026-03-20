package com.bcponline.dailyoffice

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.bcponline.dailyoffice.data.FileRegistry
import com.bcponline.dailyoffice.data.ProperFetcher
import com.bcponline.dailyoffice.data.ProperParser
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.LiturgicalDay
import kotlinx.coroutines.runBlocking
class DailyOfficeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: android.content.Context, id: GlanceId) {
        val now = java.time.LocalDateTime.now()
        val isEarlyMorning = now.hour < 4
        val date = if (isEarlyMorning)
            java.time.LocalDate.now().minusDays(1).let {
                kotlinx.datetime.LocalDate(it.year, it.monthValue, it.dayOfMonth)
            }
        else
            kotlinx.datetime.LocalDate(now.year, now.monthValue, now.dayOfMonth)

        val day: LiturgicalDay = runBlocking {
            FileRegistry.loadFiles("daily_propers")
            ProperParser.loadFilesForDate(date)
            ProperFetcher.getProperForDate(date, forceTwoReadings = Settings.forceTwoReadings, useOptionalFeasts = Settings.useOptionalSaints, useExtraFeasts = Settings.useExtraFeasts)
        }
        val office = if (isEarlyMorning || now.hour >= 16) day.evening else day.morning
        val bg = if (office.color == LiturgicalColor.NONE)
            androidx.compose.ui.graphics.Color(0xFFFFFBFE)
        else
            office.color.background

        WidgetUpdateWorker.schedule(context)
        provideContent { WidgetContent(date.toString(), office.name, office.psalter, office.firstReading, office.secondReading, bg) }
    }
}

@Composable
private fun WidgetContent(date: String, name: String, psalter: String, first: String, second: String, bg: androidx.compose.ui.graphics.Color) {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(ColorProvider(bg))
            .padding(12.dp)
    ) {
        Text(date, style = TextStyle(fontSize = 11.sp))
        Text(name, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp))
        if (psalter.isNotBlank()) Text("Psalm $psalter", style = TextStyle(fontSize = 12.sp))
        if (first.isNotBlank()) Text(first, style = TextStyle(fontSize = 12.sp))
        if (second.isNotBlank()) Text(second, style = TextStyle(fontSize = 12.sp))
    }
}

class DailyOfficeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DailyOfficeWidget()
}
