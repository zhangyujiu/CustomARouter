package com.jk.order

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.google.auto.service.AutoService
import com.jk.common.iinterface.IApplication

@AutoService(IApplication::class)
class ApplicationImpl : IApplication {

    lateinit var application: Application

    override fun onCreate(application: Application) {
        this.application = application
        Log.e("Order", "onCreate")
    }

    override fun onLowMemory() {
        Log.e("Order", "onLowMemory")
    }

    override fun onTerminate() {
        Log.e("Order", "onTerminate")
    }

    override fun onTrimMemory(level: Int) {
        Log.e("Order", "onTrimMemory")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.e("Order", "onConfigurationChanged")
    }
}