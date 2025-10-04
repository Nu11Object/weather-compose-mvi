package com.nullo.weathercompose

import android.app.Application
import com.nullo.weathercompose.di.ApplicationComponent
import com.nullo.weathercompose.di.DaggerApplicationComponent

class WeatherApp : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}
