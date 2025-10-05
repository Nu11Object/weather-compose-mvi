package com.nullo.weathercompose.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.nullo.weathercompose.WeatherApp
import com.nullo.weathercompose.presentation.root.DefaultRootComponent
import com.nullo.weathercompose.presentation.root.RootContent
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var defaultRootComponentFactory: DefaultRootComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as WeatherApp).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val defaultRootComponent = defaultRootComponentFactory.create(defaultComponentContext())

        setContent {
            RootContent(component = defaultRootComponent)
        }
    }
}
