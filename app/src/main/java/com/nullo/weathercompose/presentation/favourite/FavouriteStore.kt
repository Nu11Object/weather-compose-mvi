package com.nullo.weathercompose.presentation.favourite

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.domain.usecase.GetCurrentWeatherUseCase
import com.nullo.weathercompose.domain.usecase.GetFavouriteCitiesUseCase
import com.nullo.weathercompose.presentation.favourite.FavouriteStore.Intent
import com.nullo.weathercompose.presentation.favourite.FavouriteStore.Label
import com.nullo.weathercompose.presentation.favourite.FavouriteStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface FavouriteStore :
    Store<Intent, State, Label> {

    data class State(
        val cityItems: List<CityItem>,
    ) {
        data class CityItem(
            val city: City,
            val weatherState: WeatherState,
        )

        sealed interface WeatherState {

            data object Initial : WeatherState

            data object Loading : WeatherState

            data object Error : WeatherState

            data class Loaded(
                val tempC: Float,
                val conditionIconUrl: String,
            ) : WeatherState
        }
    }

    sealed interface Intent {

        data object ClickSearch : Intent

        data class CityItemClick(val city: City) : Intent

        data object ClickAddFavourite : Intent
    }

    sealed interface Label {

        data object SearchClicked : Label

        data class CityItemClicked(val city: City) : Label

        data object AddFavouriteClicked : Label
    }
}

class FavouriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getFavouriteCitiesUseCase: GetFavouriteCitiesUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
) : StoreFactory by storeFactory {

    fun create(): FavouriteStore =
        object : FavouriteStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FavouriteStore",
            initialState = State(emptyList()),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed interface Msg {

        data class FavouriteCitiesLoaded(val cities: List<City>) : Msg

        data class WeatherLoaded(
            val cityId: Int,
            val tempC: Float,
            val conditionIconUrl: String,
        ) : Msg

        data class WeatherLoadingError(
            val cityId: Int
        ) : Msg

        data class WeatherIsLoading(
            val cityId: Int
        ) : Msg
    }

    private sealed interface Action {

        data class FavouriteCitiesLoaded(val cities: List<City>) : Action
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getFavouriteCitiesUseCase().collect {
                    dispatch(Action.FavouriteCitiesLoaded(it))
                }
            }
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.FavouriteCitiesLoaded -> {
                    val cities = action.cities
                    dispatch(Msg.FavouriteCitiesLoaded(cities))
                    cities.forEach { city ->
                        scope.launch {
                            loadWeatherForCity(city)
                        }
                    }
                }
            }
        }

        private suspend fun loadWeatherForCity(city: City) {
            dispatch(Msg.WeatherIsLoading(city.id))
            try {
                val weather = getCurrentWeatherUseCase(city.id)
                dispatch(
                    Msg.WeatherLoaded(
                        cityId = city.id,
                        tempC = weather.tempC,
                        conditionIconUrl = weather.conditionIconUrl
                    )
                )
            } catch (_: Exception) {
                dispatch(Msg.WeatherLoadingError(city.id))
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.CityItemClick -> {
                    publish(Label.CityItemClicked(intent.city))
                }

                Intent.ClickAddFavourite -> {
                    publish(Label.AddFavouriteClicked)
                }

                Intent.ClickSearch -> {
                    publish(Label.SearchClicked)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {

        private fun List<State.CityItem>.updateWeatherState(
            cityId: Int,
            transform: (State.CityItem) -> State.CityItem
        ) = map { if (it.city.id == cityId) transform(it) else it }

        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                is Msg.FavouriteCitiesLoaded -> {
                    copy(
                        cityItems = msg.cities.map {
                            State.CityItem(
                                city = it,
                                weatherState = State.WeatherState.Initial
                            )
                        }
                    )
                }

                is Msg.WeatherIsLoading -> {
                    copy(
                        cityItems = cityItems.updateWeatherState(msg.cityId) {
                            it.copy(weatherState = State.WeatherState.Loading)
                        }
                    )
                }

                is Msg.WeatherLoaded -> {
                    copy(
                        cityItems = cityItems.updateWeatherState(msg.cityId) {
                            it.copy(
                                weatherState = State.WeatherState.Loaded(
                                    tempC = msg.tempC,
                                    conditionIconUrl = msg.conditionIconUrl
                                )
                            )
                        }
                    )
                }

                is Msg.WeatherLoadingError -> {
                    copy(
                        cityItems = cityItems.updateWeatherState(msg.cityId) {
                            it.copy(weatherState = State.WeatherState.Error)
                        }
                    )
                }
            }
        }
    }
}
