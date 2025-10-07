package com.nullo.weathercompose.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nullo.weathercompose.R
import com.nullo.weathercompose.domain.entity.Weather
import com.nullo.weathercompose.presentation.extensions.formatToFullDate
import com.nullo.weathercompose.presentation.extensions.formatToFullWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(component: DetailsComponent) {

    val model by component.model.collectAsState()

    Scaffold(
        topBar = {
            TopBarContent(
                modifier = Modifier.fillMaxWidth(),
                cityName = model.city.name,
                isFavourite = model.isFavourite,
                forecastState = model.forecastState,
                onBackClick = component::onBackClick,
                onChangeFavouriteStatusClick = component::onChangeFavouriteStatusClick,
            )
        }
    ) { innerPadding ->

        when (val state = model.forecastState) {
            DetailsStore.State.ForecastState.Initial -> {}

            DetailsStore.State.ForecastState.Error -> {
                ErrorContent(modifier = Modifier.fillMaxSize())
            }

            DetailsStore.State.ForecastState.Loading -> {
                LoadingContent(modifier = Modifier.fillMaxSize())
            }

            is DetailsStore.State.ForecastState.Loaded -> {
                ForecastContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    currentWeather = state.forecast.currentWeather,
                    upcoming = state.forecast.upcoming,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarContent(
    modifier: Modifier = Modifier,
    cityName: String,
    isFavourite: Boolean,
    forecastState: DetailsStore.State.ForecastState,
    onBackClick: () -> Unit,
    onChangeFavouriteStatusClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cityName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (forecastState is DetailsStore.State.ForecastState.Loaded) {
                    Text(
                        text = forecastState.forecast.currentWeather.date.formatToFullDate(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_back)
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onChangeFavouriteStatusClick() }
            ) {
                Icon(
                    imageVector = if (isFavourite) {
                        Icons.Rounded.Star
                    } else {
                        Icons.Rounded.StarOutline
                    },
                    contentDescription = if (isFavourite) {
                        stringResource(R.string.cd_remove_city_from_favourites)
                    } else {
                        stringResource(R.string.cd_add_city_to_favourites)
                    },
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    )
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
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.error_internet),
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ForecastContent(
    modifier: Modifier = Modifier,
    currentWeather: Weather,
    upcoming: List<Weather>,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = currentWeather.conditionText,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.template_temp_c, currentWeather.tempC),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 42.sp),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
            GlideImage(
                modifier = Modifier.size(32.dp),
                model = currentWeather.conditionIconUrl,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.title_forecast),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                upcoming.forEach {
                    ForecastItem(weather = it)
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ForecastItem(
    modifier: Modifier = Modifier,
    weather: Weather,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlideImage(
            modifier = Modifier.size(24.dp),
            model = weather.conditionIconUrl,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = weather.date.formatToFullWeek(),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.template_temp_c, weather.tempC),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
