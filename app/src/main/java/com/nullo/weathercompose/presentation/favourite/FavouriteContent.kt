package com.nullo.weathercompose.presentation.favourite

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.rounded.AddLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nullo.weathercompose.R

@Composable
fun FavouriteContent(component: FavouriteComponent) {

    val model by component.model.collectAsState()

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            SearchBar(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                onClick = component::onSearchClick
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(
                    items = model.cityItems,
                    key = { it.city.id },
                ) {
                    CityCard(
                        cityItem = it,
                        onClick = {
                            component.onCityItemClick(it.city)
                        }
                    )
                }

                item {
                    AddFavouriteCityCard(
                        onClick = component::onAddFavouriteClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CityCard(
    cityItem: FavouriteStore.State.CityItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .sizeIn(minHeight = 200.dp)
                .padding(16.dp),
        ) {
            when (val weatherState = cityItem.weatherState) {
                FavouriteStore.State.WeatherState.Initial -> {}

                FavouriteStore.State.WeatherState.Error -> {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp),
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                    )
                }

                is FavouriteStore.State.WeatherState.Loaded -> {
                    GlideImage(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(48.dp),
                        model = weatherState.conditionIconUrl,
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 24.dp),
                        text = stringResource(R.string.template_temp_c, weatherState.tempC),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 42.sp),
                    )
                }

                FavouriteStore.State.WeatherState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = cityItem.city.name,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun AddFavouriteCityCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .sizeIn(minHeight = 200.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.AddLocation,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.button_add_city),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable {
            onClick()
        },
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.hint_search_bar),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        }
    }
}
