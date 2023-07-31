package com.example.farmmate1.data

import retrofit2.http.Body
import retrofit2.http.POST


class WriteDiaryData(
    val plantUuid: String,
    val weather: Int,
    val temperature: Int,
    val humidity: Int,val waterFlag : Boolean,
    val fertlizeFlag : Boolean,
    val fertilizeName: String,
    val fertilizeUsage : String,
    val pesticideFlag : Boolean,
    val pesticideName : String,
    val pesticideUsage : String,
    val memo : String,

)

interface PlantDiaryApiService {

    @POST("http://localhost:1000/api/plant/diary") // 해당 엔드포인트로 POST 요청을 보낼 것을 명시
    fun postPlantDiary(@Body data: WriteDiaryData): retrofit2.Call<Void>
}