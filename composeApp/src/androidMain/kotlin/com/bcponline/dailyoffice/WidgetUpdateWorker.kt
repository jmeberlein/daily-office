package com.bcponline.dailyoffice

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.*
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val daily = inputData.getBoolean("daily", false)
        val manager = GlanceAppWidgetManager(applicationContext)
        if (daily) {
            val widget = LargeDailyOfficeWidget()
            manager.getGlanceIds(LargeDailyOfficeWidget::class.java).forEach { widget.update(applicationContext, it) }
        } else {
            val widget = SmallDailyOfficeWidget()
            manager.getGlanceIds(SmallDailyOfficeWidget::class.java).forEach { widget.update(applicationContext, it) }
        }
        schedule(applicationContext, daily)
        return Result.success()
    }

    companion object {
        fun schedule(context: Context, daily: Boolean) {
            val now = java.time.LocalTime.now()
            val next4 = java.time.LocalTime.of(4, 0)
            val next16 = java.time.LocalTime.of(16, 0)

            val nextTransition = if (daily) {
                // Only update at 4:00
                if (now < next4) next4 else next4
            } else {
                // Update at 4:00 and 16:00
                when {
                    now < next4  -> next4
                    now < next16 -> next16
                    else         -> next4
                }
            }

            var delayMinutes = java.time.Duration.between(now, nextTransition).toMinutes()
            if (delayMinutes <= 0) delayMinutes += 24 * 60

            val workName = if (daily) "widget_update_daily" else "widget_update_small"
            val request = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setInputData(workDataOf("daily" to daily))
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
