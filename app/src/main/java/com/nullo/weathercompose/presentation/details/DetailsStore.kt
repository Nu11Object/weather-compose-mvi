package com.nullo.weathercompose.presentation.details

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.domain.entity.Forecast
import com.nullo.weathercompose.domain.usecase.ChangeFavouriteStateUseCase
import com.nullo.weathercompose.domain.usecase.GetForecastUseCase
import com.nullo.weathercompose.domain.usecase.ObserveFavouriteStateUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DetailsStore : Store<DetailsStore.Intent, DetailsStore.State, DetailsStore.Label> {

    data class State(
        val city: City,
        val isFavourite: Boolean,
        val forecastState: ForecastState,
    ) {

        sealed interface ForecastState {

            data object Initial : ForecastState

            data object Loading : ForecastState

            data object Error : ForecastState

            data class Loaded(val forecast: Forecast) : ForecastState
        }
    }

    sealed interface Intent {

        data object ClickBack : Intent

        data object ClickChangeFavouriteStatus : Intent
    }

    sealed interface Label {

        data object BackClicked : Label
    }
}

class DetailsStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val changeFavouriteStateUseCase: ChangeFavouriteStateUseCase,
    private val observeFavouriteStateUseCase: ObserveFavouriteStateUseCase,
    private val getForecastUseCase: GetForecastUseCase,
) : StoreFactory by storeFactory {

    fun create(city: City): DetailsStore = object : DetailsStore,
        Store<DetailsStore.Intent, DetailsStore.State, DetailsStore.Label> by storeFactory.create(
            name = "DetailsStore",
            initialState = DetailsStore.State(
                city = city,
                isFavourite = false,
                forecastState = DetailsStore.State.ForecastState.Initial,
            ),
            bootstrapper = BootstrapperImpl(city),
            executorFactory = { ExecutorImpl(city) },
            reducer = ReducerImpl,
        ) {}

    sealed interface Msg {

        data object ForecastStartLoading : Msg

        data class ForecastLoaded(val forecast: Forecast) : Msg

        data object ForecastLoadingError : Msg

        data class FavouriteStatusChanged(val isFavourite: Boolean) : Msg
    }

    sealed interface Action {

        data object ForecastStartLoading : Action

        data class ForecastLoaded(val forecast: Forecast) : Action

        data object ForecastLoadingError : Action

        data class FavouriteStatusChanged(val isFavourite: Boolean) : Action
    }

    private inner class BootstrapperImpl(
        private val city: City
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                launch {
                    observeFavouriteStateUseCase(city.id).collect {
                        dispatch(Action.FavouriteStatusChanged(it))
                    }
                }
                launch {
                    dispatch(Action.ForecastStartLoading)
                    try {
                        val forecast = getForecastUseCase(city.id)
                        dispatch(Action.ForecastLoaded(forecast))
                    } catch (_: Exception) {
                        dispatch(Action.ForecastLoadingError)
                    }
                }
            }
        }
    }

    private inner class ExecutorImpl(
        private val city: City
    ) : CoroutineExecutor<DetailsStore.Intent, Action, DetailsStore.State, Msg, DetailsStore.Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.FavouriteStatusChanged -> {
                    dispatch(Msg.FavouriteStatusChanged(action.isFavourite))
                }

                is Action.ForecastLoaded -> {
                    dispatch(Msg.ForecastLoaded(action.forecast))
                }

                Action.ForecastLoadingError -> {
                    dispatch(Msg.ForecastLoadingError)
                }

                Action.ForecastStartLoading -> {
                    dispatch(Msg.ForecastStartLoading)
                }
            }
        }

        override fun executeIntent(intent: DetailsStore.Intent) {
            when (intent) {
                DetailsStore.Intent.ClickBack -> {
                    publish(DetailsStore.Label.BackClicked)
                }

                DetailsStore.Intent.ClickChangeFavouriteStatus -> {
                    scope.launch {
                        val isFavourite = state().isFavourite
                        if (isFavourite) {
                            changeFavouriteStateUseCase.removeFromFavourite(city.id)
                        } else {
                            changeFavouriteStateUseCase.addToFavourite(city)
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<DetailsStore.State, Msg> {
        override fun DetailsStore.State.reduce(msg: Msg): DetailsStore.State {
            return when (msg) {
                is Msg.FavouriteStatusChanged -> {
                    copy(isFavourite = msg.isFavourite)
                }

                is Msg.ForecastLoaded -> {
                    copy(forecastState = DetailsStore.State.ForecastState.Loaded(msg.forecast))
                }

                Msg.ForecastLoadingError -> {
                    copy(forecastState = DetailsStore.State.ForecastState.Error)
                }

                Msg.ForecastStartLoading -> {
                    copy(forecastState = DetailsStore.State.ForecastState.Loading)
                }
            }
        }
    }
}
