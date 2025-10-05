package com.nullo.weathercompose.presentation.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.domain.usecase.ChangeFavouriteStateUseCase
import com.nullo.weathercompose.domain.usecase.SearchCityUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchStore : Store<SearchStore.Intent, SearchStore.State, SearchStore.Label> {

    data class State(
        val query: String,
        val searchState: SearchState,
    ) {

        sealed interface SearchState {

            data object Initial : SearchState

            data object Loading : SearchState

            data object EmptyResult : SearchState

            data object Error : SearchState

            data class Loaded(val cities: List<City>) : SearchState
        }
    }

    sealed interface Intent {

        data class ChangeQuery(val query: String) : Intent

        data object ClickSearch : Intent

        data object ClickBack : Intent

        data class ClickCity(val city: City) : Intent
    }

    sealed interface Label {

        data object BackClicked : Label

        data object SavedToFavourite : Label

        data class SelectedForForecast(val city: City) : Label
    }
}

class SearchStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
    private val searchCityUseCase: SearchCityUseCase,
) : StoreFactory by storeFactory {

    fun create(openReason: OpenReason): SearchStore = object : SearchStore,
        Store<SearchStore.Intent, SearchStore.State, SearchStore.Label> by storeFactory.create(
            name = "SearchStore",
            initialState = SearchStore.State(
                query = "",
                searchState = SearchStore.State.SearchState.Initial
            ),
            executorFactory = { ExecutorImpl(openReason) },
            reducer = ReducerImpl,
        ) {}

    private sealed interface Action

    private sealed interface Msg {

        data class QueryUpdated(val query: String) : Msg

        data object LoadingStarted : Msg

        data object SearchFailed : Msg

        data object SearchReturnedEmpty : Msg

        data class SearchSucceeded(val cities: List<City>) : Msg
    }

    private inner class ExecutorImpl(
        private val openReason: OpenReason,
    ) : CoroutineExecutor<SearchStore.Intent, Action, SearchStore.State, Msg, SearchStore.Label>() {

        private var searchJob: Job? = null

        override fun executeIntent(intent: SearchStore.Intent) {
            when (intent) {
                is SearchStore.Intent.ChangeQuery -> {
                    dispatch(Msg.QueryUpdated(intent.query))
                }

                SearchStore.Intent.ClickBack -> {
                    publish(SearchStore.Label.BackClicked)
                }

                is SearchStore.Intent.ClickCity -> {
                    when (openReason) {
                        OpenReason.AddToFavourite -> {
                            scope.launch {
                                changeFavouriteStateUseCase.addToFavourite(intent.city)
                                publish(SearchStore.Label.SavedToFavourite)
                            }
                        }

                        OpenReason.RegularSearch -> {
                            publish(SearchStore.Label.SelectedForForecast(intent.city))
                        }
                    }
                }

                is SearchStore.Intent.ClickSearch -> {
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        try {
                            dispatch(Msg.LoadingStarted)
                            val cities = searchCityUseCase(state().query)
                            if (cities.isEmpty()) {
                                dispatch(Msg.SearchReturnedEmpty)
                            } else {
                                dispatch(Msg.SearchSucceeded(cities))
                            }
                        } catch (_: Exception) {
                            dispatch(Msg.SearchFailed)
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<SearchStore.State, Msg> {
        override fun SearchStore.State.reduce(msg: Msg): SearchStore.State {
            return when (msg) {
                Msg.LoadingStarted -> {
                    copy(searchState = SearchStore.State.SearchState.Loading)
                }

                is Msg.QueryUpdated -> {
                    copy(query = msg.query)
                }

                Msg.SearchFailed -> {
                    copy(searchState = SearchStore.State.SearchState.Error)
                }

                Msg.SearchReturnedEmpty -> {
                    copy(searchState = SearchStore.State.SearchState.EmptyResult)
                }

                is Msg.SearchSucceeded -> {
                    copy(searchState = SearchStore.State.SearchState.Loaded(msg.cities))
                }
            }
        }
    }
}
