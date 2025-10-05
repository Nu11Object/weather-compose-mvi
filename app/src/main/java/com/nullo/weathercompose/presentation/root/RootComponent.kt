package com.nullo.weathercompose.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.nullo.weathercompose.presentation.details.DetailsComponent
import com.nullo.weathercompose.presentation.favourite.FavouriteComponent
import com.nullo.weathercompose.presentation.search.SearchComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        data class Details(val component: DetailsComponent) : Child

        data class Favourite(val component: FavouriteComponent) : Child

        data class Search(val component: SearchComponent) : Child
    }
}
