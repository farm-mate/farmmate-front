package com.example.farmmate1.network

import com.example.farmmate1.data.WriteDiaryData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

abstract interface WriteDiaryInterface {
    @POST("https://bd301c46-a122-477e-ace3-1d7b47fa9610.mock.pstmn.io")
    abstract // 실제 서버 엔드포인트를 여기에 입력하세요
    fun sendData(@Body data: WriteDiaryData): Call<Void>

}