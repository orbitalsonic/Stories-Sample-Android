package com.orbitalsonic.storiessample

import android.app.Application
import com.orbitalsonic.storiessample.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}