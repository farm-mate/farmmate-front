package com.example.farmmate1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor



object RetrofitClient {
    //에뮬레이터
    //private const val BASE_URL = "http://10.0.2.2:3000/api/"
    //기기
    private const val BASE_URL = "http://172.20.6.232:3000/api/"

    // HttpLoggingInterceptor 생성
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 로그 레벨 설정 (BASIC, HEADERS, BODY 등)
    }

    // OkHttpClient에 인터셉터 추가
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Retrofit 요청과 응답의 로그를 보기 위해 추가
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // OkHttpClient 설정 추가
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}