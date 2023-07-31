package com.example.farmmate1.network

import com.example.farmmate1.data.TodoItem
import com.example.farmmate1.data.WriteDiaryData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ToDoListInterface {
    // API 엔드포인트에 맞게 적절히 정의해주세요
    // 예를 들어, 식물 다이어리 생성을 위한 API라면 다음과 같이 정의할 수 있습니다.
    @POST("api/plant/diary/writediary")
    @Headers("Content-Type: application/json")
    fun sendData(@Body data: HashMap<String, Any>): Call<Void>

    companion object {
        fun create(): ToDoListInterface {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://localhost:1000/") // 서버의 기본 URL 설정
                .addConverterFactory(GsonConverterFactory.create()) // Gson을 사용해 JSON 데이터를 파싱
                .build()

            return retrofit.create(ToDoListInterface::class.java)
        }

        fun sendData(data: HashMap<String, Any>): Any {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://localhost:1000/") // 서버의 기본 URL 설정
                .addConverterFactory(GsonConverterFactory.create()) // Gson을 사용해 JSON 데이터를 파싱
                .build()

            return retrofit.create(ToDoListInterface::class.java)

        }
    }
}








