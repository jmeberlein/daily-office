package com.bcponline.dailyoffice

import android.app.Application

class DailyOfficeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Settings.init(this)
        WidgetUpdateWorker.schedule(this, daily = false)
        WidgetUpdateWorker.schedule(this, daily = true)
    }
}
