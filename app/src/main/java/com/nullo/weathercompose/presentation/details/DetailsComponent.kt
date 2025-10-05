package com.nullo.weathercompose.presentation.details

import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {

    val model: StateFlow<DetailsStore.State>

    fun onBackClick()

    fun onChangeFavouriteStatusClick()
}
