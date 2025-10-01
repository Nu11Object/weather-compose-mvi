package com.nullo.weathercompose.data.network.api

import com.nullo.weathercompose.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiProvider {

    private const val BASE_URL = "https://api.weatherapi.com/v1/"
    private const val QUERY_API_KEY = "key"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val newUrl = request.url
                .newBuilder()
                .addQueryParameter(QUERY_API_KEY, BuildConfig.WEATHER_API_KEY)
                .build()
            val newRequest = request
                .newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create()
}
