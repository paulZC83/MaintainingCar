package com.example.maintainingcar

import android.app.Application
import android.content.Context

class CarApplication: Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}