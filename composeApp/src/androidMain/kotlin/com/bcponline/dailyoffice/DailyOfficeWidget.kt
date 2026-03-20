package com.bcponline.dailyoffice

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.bcponline.dailyoffice.data.FileRegistry
import com.bcponline.dailyoffice.data.ProperFetcher
import com.bcponline.dailyoffice.data.ProperParser
import com.bcponline.dailyoffice.model.LiturgicalColor
import com.bcponline.dailyoffice.model.LiturgicalDay
import com.bcponline.dailyoffice.model.Office
import kotlinx.coroutines.runBlocking

private fun bgColor(color: LiturgicalColor) =
    if (color == LiturgicalColor.NONE) Color(0xFFFFFBFE) else color.background

private fun fgColor(color: LiturgicalColor) =
    if (color == LiturgicalColor.NONE) Color(0xFF1C1B1F) else color.onBackground

private fun loadDay(context: Context): Pair<kotlinx.datetime.LocalDate, LiturgicalDay> {
    val now = java.time.LocalDateTime.now()
    val javaDate = if (now.hour < 4) java.time.LocalDate.now().minusDays(1) else java.time.LocalDate.now()
    val date = kotlinx.datetime.LocalDate(javaDate.year, javaDate.monthValue, javaDate.dayOfMonth)
    val day = runBlocking {
        FileRegistry.loadFiles("daily_propers")
        ProperParser.loadFilesForDate(date)
        ProperFetcher.getProperForDate(date, Settings.forceTwoReadings, Settings.useOptionalSaints, Settings.useExtraFeasts)
    }
    return date to day
}

@Composable
private fun OfficeSection(office: Office) {
    val fg = ColorProvider(fgColor(office.color))
    Text(office.name, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = fg))
    if (office.psalter.isNotBlank()) Text("Ps. ${office.psalter}", style = TextStyle(fontSize = 11.sp, color = fg))
    if (office.firstReading.isNotBlank()) Text(office.firstReading, style = TextStyle(fontSize = 11.sp, color = fg))
    if (office.secondReading.isNotBlank()) Text(office.secondReading, style = TextStyle(fontSize = 11.sp, color = fg))
}

// ── Small widget: current office, square ──────────────────────────────────────

class SmallDailyOfficeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val now = java.time.LocalDateTime.now()
        val (date, day) = loadDay(context)
        val office = if (now.hour < 4 || now.hour >= 16) day.evening else day.morning
        val bg = bgColor(office.color)
        val fg = fgColor(office.color)

        WidgetUpdateWorker.schedule(context, daily = false)
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().background(ColorProvider(bg)).padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    date.toString(),
                    style = TextStyle(fontSize = 11.sp, color = ColorProvider(fg))
                )
                OfficeSection(office)
            }
        }
    }
}

class SmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SmallDailyOfficeWidget()
}

// ── Large widget: full day, 2:1 ───────────────────────────────────────────────

class LargeDailyOfficeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val (date, day) = loadDay(context)
        val morningBg = bgColor(day.morning.color)
        val morningFg = fgColor(day.morning.color)

        WidgetUpdateWorker.schedule(context, daily = true)
        provideContent {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                // Date bar — morning color
                Box(
                    modifier = GlanceModifier.fillMaxWidth().background(ColorProvider(morningBg)).padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(date.toString(), style = TextStyle(fontSize = 12.sp, color = ColorProvider(morningFg)))
                }
                // Morning / Evening side by side
                Row(modifier = GlanceModifier.fillMaxSize()) {
                    Column(
                        modifier = GlanceModifier.defaultWeight()
                            .fillMaxHeight()
                            .background(ColorProvider(morningBg))
                            .padding(10.dp)
                    ) {
                        OfficeSection(day.morning)
                    }
                    Column(
                        modifier = GlanceModifier.defaultWeight()
                            .fillMaxHeight()
                            .background(ColorProvider(bgColor(day.evening.color)))
                            .padding(10.dp)
                    ) {
                        OfficeSection(day.evening)
                    }
                }
            }
        }
    }
}

class LargeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = LargeDailyOfficeWidget()
}
