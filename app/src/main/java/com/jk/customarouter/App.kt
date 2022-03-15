package com.jk.customarouter

import android.app.Application
import android.content.res.Configuration
import com.jk.common.iinterface.IApplication
import java.util.*

class App : Application() {
    private var mApplicationList: List<IApplication> = ServiceLoader.load(IApplication::class.java, javaClass.classLoader).toList()

    override fun onCreate() {
        super.onCreate()
        mApplicationList.forEach {
            it.onCreate(this)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mApplicationList.forEach {
            it.onLowMemory()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        mApplicationList.forEach {
            it.onTerminate()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        mApplicationList.forEach {
            it.onTrimMemory(level)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mApplicationList.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }
}