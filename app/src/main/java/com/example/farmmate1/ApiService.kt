package com.example.farmmate1

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("plants")
    fun getPlantList(): Call<List<Plant>>
    // 서버에서 식물 정보를 가져오는 GET 요청을 정의

    @POST("plants")
    @Headers("Content-Type: application/json")
    fun createPlant(@Body plant: Plant): Call<Plant>

    @GET("plants")
    fun getPlant(): Call<Plant>


//    @GET("plants")
//    suspend fun getPlants(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<Plant>>

}