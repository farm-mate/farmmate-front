package com.example.farmmate1.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherObject {
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getRetroFitService():WeatherInterface{
        return getRetrofit().create(WeatherInterface::class.java)
    }
}