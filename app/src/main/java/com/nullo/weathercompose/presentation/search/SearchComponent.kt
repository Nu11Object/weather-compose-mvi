package com.nullo.weathercompose.presentation.search

import com.nullo.weathercompose.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface SearchComponent {

    val model: StateFlow<SearchStore.State>

    fun onQueryChange(query: String)

    fun onBackClick()

    fun onSearchClick()

    fun onCityClick(city: City)
}
