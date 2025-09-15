package com.orbitalsonic.storiessample

import android.app.Application
import com.orbitalsonic.storiessample.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(KoinModules().moduleList)
        }
    }
}