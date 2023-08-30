package com.example.farmmate1

import com.example.farmmate1.data.TodoItem
import com.example.farmmate1.network.ToDoListInterface
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @POST("device")
    @Headers("Content-Type: application/json")
    fun saveDiviceInfo(@Body deviceId: String): Call<Void>

    @GET("plant")
    fun getPlantList(): Call<List<Plant>>
    // 서버에서 식물 정보를 가져오는 GET 요청을 정의

    @POST("plant")
    @Headers("Content-Type: application/json")
    fun postPlant(@Body plant: Plant): Call<Plant>

    @GET("plant")
    fun getPlant(): Call<List<Plant>>


    @GET("history")
    fun getHistoryList(): Call<List<History>>
    // 서버에서 진단 결과를 가져오는 GET 요청을 정의

    @POST("history")
    @Headers("Content-Type: application/json")
    fun saveResult(@Body history: History): Call<History>

    @GET("history")
    fun getHistory(): Call<History>


}