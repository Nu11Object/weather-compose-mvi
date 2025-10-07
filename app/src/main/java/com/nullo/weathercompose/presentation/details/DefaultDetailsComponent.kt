package com.nullo.weathercompose.presentation.details

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

class DefaultDetailsComponent @AssistedInject constructor(
    private val detailsStoreFactory: DetailsStoreFactory,
    @Assisted(KEY_CITY) private val city: City,
    @Assisted(KEY_ON_BACK_CLICKED) private val onBackClicked: () -> Unit,
    @Assisted(KEY_COMPONENT_CONTEXT) componentContext: ComponentContext,
) : DetailsComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { detailsStoreFactory.create(city) }

    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    DetailsStore.Label.BackClicked -> {
                        onBackClicked()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<DetailsStore.State> = store.stateFlow

    override fun onBackClick() {
        store.accept(DetailsStore.Intent.ClickBack)
    }

    override fun onChangeFavouriteStatusClick() {
        store.accept(DetailsStore.Intent.ClickChangeFavouriteStatus)
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted(KEY_CITY) city: City,
            @Assisted(KEY_ON_BACK_CLICKED) onBackClicked: () -> Unit,
            @Assisted(KEY_COMPONENT_CONTEXT) componentContext: ComponentContext,
        ): DefaultDetailsComponent
    }

    companion object {

        private const val KEY_CITY = "city"
        private const val KEY_ON_BACK_CLICKED = "onBackClicked"
        private const val KEY_COMPONENT_CONTEXT = "componentContext"
    }
}
