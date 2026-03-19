package com.bcponline.dailyoffice

import android.app.Application

class DailyOfficeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Settings.init(this)
    }
}
