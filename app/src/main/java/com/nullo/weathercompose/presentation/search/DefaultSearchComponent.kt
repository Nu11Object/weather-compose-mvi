package com.nullo.weathercompose.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.presentation.extensions.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultSearchComponent @Inject constructor(
    private val openReason: OpenReason,
    private val searchStoreFactory: SearchStoreFactory,
    private val onBackClicked: () -> Unit,
    private val onCitySavedToFavourite: () -> Unit,
    private val onCitySelectedForForecast: (City) -> Unit,
    componentContext: ComponentContext,
) : SearchComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { searchStoreFactory.create(openReason) }

    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    SearchStore.Label.BackClicked -> ::onBackClicked
                    SearchStore.Label.SavedToFavourite -> ::onCitySavedToFavourite
                    is SearchStore.Label.SelectedForForecast -> ::onCitySelectedForForecast
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<SearchStore.State> = store.stateFlow

    override fun onQueryChange(query: String) {
        store.accept(SearchStore.Intent.ChangeQuery(query))
    }

    override fun onBackClick() {
        store.accept(SearchStore.Intent.ClickBack)
    }

    override fun onSearchClick() {
        store.accept(SearchStore.Intent.ClickSearch)
    }

    override fun onCityClick(city: City) {
        store.accept(SearchStore.Intent.ClickCity(city))
    }
}
