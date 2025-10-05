package com.nullo.weathercompose.presentation.favourite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.presentation.extensions.componentScope
import com.nullo.weathercompose.presentation.favourite.FavouriteStore.Intent
import com.nullo.weathercompose.presentation.favourite.FavouriteStore.Label
import com.nullo.weathercompose.presentation.favourite.FavouriteStore.State
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultFavouriteComponent @AssistedInject constructor(
    private val favouriteStoreFactory: FavouriteStoreFactory,
    @Assisted(KEY_ON_ADD_FAVOURITE) private val onAddFavouriteClicked: () -> Unit,
    @Assisted(KEY_ON_CITY_ITEM) private val onCityItemClicked: (City) -> Unit,
    @Assisted(KEY_ON_SEARCH) private val onSearchClicked: () -> Unit,
    @Assisted(KEY_COMPONENT_CONTEXT) componentContext: ComponentContext,
) : FavouriteComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { favouriteStoreFactory.create() }

    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    Label.AddFavouriteClicked -> ::onAddFavouriteClicked
                    is Label.CityItemClicked -> ::onCityItemClicked
                    Label.SearchClicked -> ::onSearchClicked
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<State> = store.stateFlow

    override fun onSearchClick() {
        store.accept(Intent.ClickSearch)
    }

    override fun onAddFavouriteClick() {
        store.accept(Intent.ClickAddFavourite)
    }

    override fun onCityItemClick(city: City) {
        store.accept(Intent.CityItemClick(city))
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted(KEY_ON_ADD_FAVOURITE) onAddFavouriteClicked: () -> Unit,
            @Assisted(KEY_ON_CITY_ITEM) onCityItemClicked: (City) -> Unit,
            @Assisted(KEY_ON_SEARCH) onSearchClicked: () -> Unit,
            @Assisted(KEY_COMPONENT_CONTEXT) componentContext: ComponentContext,
        ): DefaultFavouriteComponent
    }

    companion object {

        private const val KEY_ON_ADD_FAVOURITE = "onAddFavouriteClicked"
        private const val KEY_ON_CITY_ITEM = "onCityItemClicked"
        private const val KEY_ON_SEARCH = "onSearchClicked"
        private const val KEY_COMPONENT_CONTEXT = "componentContext"
    }
}
