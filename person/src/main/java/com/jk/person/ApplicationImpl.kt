package com.jk.person

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
        Log.e("Person", "onCreate")
    }

    override fun onLowMemory() {
        Log.e("Person", "onLowMemory")
    }

    override fun onTerminate() {
        Log.e("Person", "onTerminate")
    }

    override fun onTrimMemory(level: Int) {
        Log.e("Person", "onTrimMemory")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.e("Person", "onConfigurationChanged")
    }
}