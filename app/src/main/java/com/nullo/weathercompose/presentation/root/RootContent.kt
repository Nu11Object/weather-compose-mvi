package com.nullo.weathercompose.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.nullo.weathercompose.presentation.details.DetailsContent
import com.nullo.weathercompose.presentation.favourite.FavouriteContent
import com.nullo.weathercompose.presentation.search.SearchContent
import com.nullo.weathercompose.presentation.ui.theme.WeatherComposeTheme

@Composable
fun RootContent(component: RootComponent) {
    WeatherComposeTheme {
        Children(stack = component.stack) {
            when (val instance = it.instance) {
                is RootComponent.Child.Details -> {
                    DetailsContent(instance.component)
                }

                is RootComponent.Child.Favourite -> {
                    FavouriteContent(instance.component)
                }

                is RootComponent.Child.Search -> {
                    SearchContent(instance.component)
                }
            }
        }
    }
}
