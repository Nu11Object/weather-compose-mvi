package com.nullo.weathercompose.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nullo.weathercompose.R
import com.nullo.weathercompose.domain.entity.City

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(component: SearchComponent) {

    val model by component.model.collectAsState()

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SearchBar(
        modifier = Modifier.focusRequester(focusRequester),
        inputField = {
            SearchBarDefaults.InputField(
                query = model.query,
                onQueryChange = { component.onQueryChange(it) },
                onSearch = { component.onSearchClick() },
                expanded = true,
                onExpandedChange = {},
                placeholder = {
                    Text(
                        text = stringResource(R.string.hint_search_bar),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                leadingIcon = {
                    IconButton(
                        onClick = { component.onBackClick() },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.cd_navigate_back),
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { component.onSearchClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.cd_search_city)
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                )
            )
        },
        expanded = true,
        onExpandedChange = {},
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        when (val searchState = model.searchState) {
            SearchStore.State.SearchState.Initial -> {}

            SearchStore.State.SearchState.EmptyResult -> {
                EmptyContent(modifier = Modifier.fillMaxSize())
            }

            SearchStore.State.SearchState.Error -> {
                ErrorContent(modifier = Modifier.fillMaxSize())
            }

            SearchStore.State.SearchState.Loading -> {
                LoadingContent(modifier = Modifier.fillMaxSize())
            }

            is SearchStore.State.SearchState.Loaded -> {
                SearchResultsContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    cities = searchState.cities,
                    onCityClick = component::onCityClick,
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = Icons.Default.LocationOff,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.error_empty_search_result),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = Icons.Default.WifiOff,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.error_internet),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun SearchResultsContent(
    modifier: Modifier = Modifier,
    cities: List<City>,
    onCityClick: (City) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(items = cities, key = { it.id }) {
            City(
                modifier = Modifier.fillMaxWidth(),
                city = it,
                onCityClick = { onCityClick(it) }
            )
        }
    }
}

@Composable
private fun City(
    modifier: Modifier = Modifier,
    city: City,
    onCityClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable { onCityClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = city.country,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
