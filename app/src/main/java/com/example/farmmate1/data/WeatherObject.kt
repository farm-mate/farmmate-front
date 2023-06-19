package com.example.farmmate1.data

import com.example.farmmate1.network.WeatherInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherObject {
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getRetrofitService(): WeatherInterface {
        return getRetrofit().create(WeatherInterface::class.java)
    }
}