package com.nullo.weathercompose.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.presentation.extensions.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultSearchComponent @AssistedInject constructor(
    private val searchStoreFactory: SearchStoreFactory,
    @Assisted(KEY_OPEN_REASON) private val openReason: OpenReason,
    @Assisted(KEY_ON_BACK_CLICKED) private val onBackClicked: () -> Unit,
    @Assisted(KEY_ON_CITY_SAVED) private val onCitySavedToFavourite: () -> Unit,
    @Assisted(KEY_ON_CITY_SELECTED) private val onCitySelectedForForecast: (City) -> Unit,
    @Assisted(KEY_COMPONENT_CONTEXT) componentContext: ComponentContext,
) : SearchComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { searchStoreFactory.create(openReason) }

    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    SearchStore.Label.BackClicked -> {
                        onBackClicked()
                    }

                    SearchStore.Label.SavedToFavourite -> {
                        onCitySavedToFavourite()
                    }

                    is SearchStore.Label.SelectedForForecast -> {
                        onCitySelectedForForecast(it.city)
                    }
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

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted(KEY_OPEN_REASON) openReason: OpenReason,
            @Assisted(KEY_ON_BACK_CLICKED) onBackClicked: () -> Unit,
            @Assisted(KEY_ON_CITY_SAVED) onCitySavedToFavourite: () -> Unit,
            @Assisted(KEY_ON_CITY_SELECTED) onCitySelectedForForecast: (City) -> Unit,
            @Assisted(KEY_COMPONENT_CONTEXT) componentContext: ComponentContext,
        ): DefaultSearchComponent
    }

    companion object {

        private const val KEY_OPEN_REASON = "openReason"
        private const val KEY_ON_BACK_CLICKED = "onBackClicked"
        private const val KEY_ON_CITY_SAVED = "onCitySavedToFavourite"
        private const val KEY_ON_CITY_SELECTED = "onCitySelectedForForecast"
        private const val KEY_COMPONENT_CONTEXT = "componentContext"
    }
}
