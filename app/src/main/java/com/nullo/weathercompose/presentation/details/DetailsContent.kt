package com.nullo.weathercompose.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nullo.weathercompose.R
import com.nullo.weathercompose.domain.entity.UpcomingItem
import com.nullo.weathercompose.domain.entity.Weather
import com.nullo.weathercompose.presentation.extensions.formatToFullDate
import com.nullo.weathercompose.presentation.extensions.formatToFullWeek
import com.nullo.weathercompose.presentation.extensions.toDetailsList

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

@Composable
private fun ForecastContent(
    modifier: Modifier = Modifier,
    currentWeather: Weather,
    upcoming: List<UpcomingItem>,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        TempAndConditionSection(
            modifier = Modifier.padding(horizontal = 8.dp),
            currentWeather = currentWeather
        )
        Spacer(modifier = Modifier.weight(1f))
        UpcomingSection(upcoming = upcoming)
        Spacer(modifier = Modifier.height(8.dp))
        DetailsSection(currentWeather = currentWeather)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.powered_by_api),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TempAndConditionSection(
    modifier: Modifier = Modifier,
    currentWeather: Weather,
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = currentWeather.conditionText,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.template_temperature, currentWeather.tempC),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 42.sp),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GlideImage(
            modifier = Modifier.fillMaxHeight(),
            model = currentWeather.conditionIconUrl,
            contentDescription = null,
        )
    }
}

@Composable
fun UpcomingSection(
    modifier: Modifier = Modifier,
    upcoming: List<UpcomingItem>,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(R.string.title_forecast),
                style = MaterialTheme.typography.bodyLarge,
            )
            upcoming.forEach {
                ForecastListItem(upcomingItem = it)
            }
        }
    }
}

@Composable
fun DetailsSection(
    modifier: Modifier = Modifier,
    currentWeather: Weather,
) {
    val details = currentWeather.toDetailsList()

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(
            items = details,
            key = { it.nameStringRes }
        ) { detail ->
            DetailsItem(
                name = stringResource(detail.nameStringRes),
                value = detail.value,
                icon = detail.icon
            )
        }
    }
}

@Composable
fun DetailsItem(
    modifier: Modifier = Modifier,
    name: String,
    value: String,
    icon: ImageVector,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ForecastListItem(
    modifier: Modifier = Modifier,
    upcomingItem: UpcomingItem,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlideImage(
            modifier = Modifier.size(24.dp),
            model = upcomingItem.conditionIconUrl,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = upcomingItem.date.formatToFullWeek(),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.width(42.dp),
            text = stringResource(R.string.template_temperature, upcomingItem.minTempC),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        HorizontalDivider(
            modifier = Modifier.width(100.dp),
        )
        Text(
            modifier = Modifier.width(42.dp),
            text = stringResource(R.string.template_temperature, upcomingItem.maxTempC),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}
