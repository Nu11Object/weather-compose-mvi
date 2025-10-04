package com.nullo.weathercompose.di

import android.content.Context
import com.nullo.weathercompose.data.local.db.FavouriteCitiesDao
import com.nullo.weathercompose.data.local.db.FavouriteDatabase
import com.nullo.weathercompose.data.network.api.ApiProvider
import com.nullo.weathercompose.data.network.api.ApiService
import com.nullo.weathercompose.data.repository.FavouriteRepositoryImpl
import com.nullo.weathercompose.data.repository.SearchRepositoryImpl
import com.nullo.weathercompose.data.repository.WeatherRepositoryImpl
import com.nullo.weathercompose.domain.repository.FavouriteRepository
import com.nullo.weathercompose.domain.repository.SearchRepository
import com.nullo.weathercompose.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @[Binds ApplicationScope]
    fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

    @[Binds ApplicationScope]
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @[Binds ApplicationScope]
    fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {

        @[Provides ApplicationScope]
        fun provideApiService(): ApiService = ApiProvider.apiService

        @[Provides ApplicationScope]
        fun provideFavouriteDatabase(context: Context): FavouriteDatabase {
            return FavouriteDatabase.getInstance(context)
        }

        @[Provides ApplicationScope]
        fun provideFavouriteDao(database: FavouriteDatabase): FavouriteCitiesDao {
            return database.getFavouriteCitiesDao()
        }
    }
}
