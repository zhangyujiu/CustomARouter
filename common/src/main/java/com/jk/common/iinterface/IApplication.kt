package com.jk.common.iinterface

import android.app.Application
import android.content.res.Configuration

interface IApplication {
    fun onCreate(application: Application)

    fun onLowMemory()

    fun onTerminate()

    fun onTrimMemory(level: Int)

    fun onConfigurationChanged(newConfig: Configuration)
}