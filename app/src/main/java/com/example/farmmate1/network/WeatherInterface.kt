package com.example.farmmate1.network


import com.example.farmmate1.data.WEATHER
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {
    //초 단기 예보조회 인증키 = getUltraSrtFcst

    //@GET("TF4qcEfT6ObRPZkI8XPooCNJDS39DY2HhujIugD+Gc6NxharY00o5rb4baYR04I+oRwTK5wpVKjQoKf5tC9Vzw==")
    @GET("TF4qcEfT6ObRPZkI8XPooCNJDS39DY2HhujIugD%2BGc6NxharY00o5rb4baYR04I%2BoRwTK5wpVKjQoKf5tC9Vzw%3D%3D")
    fun getWeather(
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("dataType") data_type: String,
        @Query("base_date") base_date: String,
        @Query("base_time") base_time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Call<WEATHER>
}