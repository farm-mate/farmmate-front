package com.example.farmmate1.network

import com.example.farmmate1.data.ApiData
import com.example.farmmate1.data.WriteDiaryData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WriteDiaryInterface {
    /*
    @POST("https://bd301c46-a122-477e-ace3-1d7b47fa9610.mock.pstmn.io")
    abstract // 실제 서버 엔드포인트를 여기에 입력하세요
    fun sendData(@Body data: WriteDiaryData): Call<Void>

}*/
    @POST("http://localhost:1000/api/plant/diary")
    @Headers("Content-Type: application/json")
    fun sendData(@Body data: ApiData): Call<Void>
    //ApiData

    companion object {
        fun create(): WriteDiaryInterface {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://localhost:1000/") // 서버의 기본 URL 설정
                .addConverterFactory(GsonConverterFactory.create()) // Gson을 사용해 JSON 데이터를 파싱
                .build()

            return retrofit.create(WriteDiaryInterface::class.java)
        }
    }
}