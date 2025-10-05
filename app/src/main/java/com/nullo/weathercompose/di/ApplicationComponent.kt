package com.nullo.weathercompose.di

import android.content.Context
import com.nullo.weathercompose.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        DataModule::class,
        PresentationModule::class,
    ]
)
@ApplicationScope
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context,
        ): ApplicationComponent
    }
}