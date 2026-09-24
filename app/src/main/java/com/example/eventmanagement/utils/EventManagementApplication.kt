package com.example.eventmanagement.utils

import android.app.Application

class EventManagementApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.applyTheme(this)
    }
}