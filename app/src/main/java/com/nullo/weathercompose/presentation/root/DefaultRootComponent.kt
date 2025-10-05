package com.nullo.weathercompose.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.presentation.details.DefaultDetailsComponent
import com.nullo.weathercompose.presentation.favourite.DefaultFavouriteComponent
import com.nullo.weathercompose.presentation.search.DefaultSearchComponent
import com.nullo.weathercompose.presentation.search.OpenReason
import kotlinx.serialization.Serializable
import javax.inject.Inject

class DefaultRootComponent @Inject constructor(
    private val defaultDetailsComponentFactory: DefaultDetailsComponent.Factory,
    private val defaultFavouriteComponentFactory: DefaultFavouriteComponent.Factory,
    private val defaultSearchComponentFactory: DefaultSearchComponent.Factory,
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Favourite,
        handleBackButton = true,
        childFactory = ::child
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            is Config.Details -> {
                val component = defaultDetailsComponentFactory.create(
                    city = config.city,
                    onBackClicked = {
                        navigation.pop()
                    },
                    componentContext = componentContext,
                )
                RootComponent.Child.Details(component)
            }

            Config.Favourite -> {
                val component = defaultFavouriteComponentFactory.create(
                    onAddFavouriteClicked = {
                        navigation.push(Config.Search(OpenReason.AddToFavourite))
                    },
                    onCityItemClicked = {
                        navigation.push(Config.Details(it))
                    },
                    onSearchClicked = {
                        navigation.push(Config.Search(OpenReason.RegularSearch))
                    },
                    componentContext = componentContext,
                )
                RootComponent.Child.Favourite(component)
            }

            is Config.Search -> {
                val component = defaultSearchComponentFactory.create(
                    openReason = config.openReason,
                    onBackClicked = {
                        navigation.pop()
                    },
                    onCitySavedToFavourite = {
                        navigation.pop()
                    },
                    onCitySelectedForForecast = {
                        navigation.push(Config.Details(it))
                    },
                    componentContext = componentContext,
                )
                RootComponent.Child.Search(component)
            }
        }
    }

    @Serializable
    sealed interface Config {

        @Serializable
        data class Details(val city: City) : Config

        @Serializable
        data object Favourite : Config

        @Serializable
        data class Search(val openReason: OpenReason) : Config
    }
}
