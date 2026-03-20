package com.bcponline.dailyoffice

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.*
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        DailyOfficeWidget().updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "widget_update"

        fun schedule(context: Context) {
            val now = java.time.LocalTime.now()
            // Next transition: 4:00 or 16:00
            val next4 = java.time.LocalTime.of(4, 0)
            val next16 = java.time.LocalTime.of(16, 0)
            val nextTransition = when {
                now < next4  -> next4
                now < next16 -> next16
                else         -> next4.plusHours(24 - now.hour.toLong())
            }
            val delayMinutes = java.time.Duration.between(now, nextTransition).toMinutes()
                .let { if (it < 0) it + 24 * 60 else it }

            val request = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
