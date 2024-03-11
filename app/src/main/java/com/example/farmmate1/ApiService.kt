package com.example.farmmate1

import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiService {

    @POST("device")
    @Headers("Content-Type: application/json")
    fun saveDeviceInfo(@Body deviceInfo: DeviceInfo): Call<Void>

    @GET("plant/device/{deviceId}")
    fun getPlantList(@Path("deviceId") deviceId: String): Call<List<PlantGet>>
    // 서버에서 식물 정보를 가져오는 GET 요청을 정의

    @Multipart
    @POST("plant")
    fun postPlant(
        @PartMap data: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part? // 이미지 파일을 MultipartBody.Part로 전송
    ): Call<PlantPost>

    @GET("plant/{plantUuid}")
    fun getPlant(@Path("plantUuid") plantUuid: String): Call<PlantGet>

    @POST("plant/{plantUuid}/bookmark")
    fun postBookmark(@Path("plantUuid")plantUuid: String): Call<Void>

    @GET("plant/device/{deviceId}/bookmark")
    fun getBookmark(@Path("deviceId")deviceId: String): Call<List<PlantGet>>

    // 일지 post 요청
    @Multipart
    @POST("diary")
    fun postDiary(
        //@Query("plant_uuid") plantUuid: String?,
        //@Path("plantUuid") plantUuid: String,
        @PartMap data: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part? // 이미지 파일을 MultipartBody.Part로 전송
    ): Call<DiaryPost>

    // 일지 GET 요청
    @GET("")
    fun getDiary(): Call<DiaryPost>

    // 모든 일지 GET 요청
    @GET("diary")
    //fun getDiaryList(@Query("plant.plant_name") plantName: String?): Call<List<DiaryGet>>
    fun getDiaryList(): Call<List<DiaryGet>>

    // 진단 POST 요청
    @Multipart
    @POST("plant/diagnose")
    fun postDiagnosis(
        @Part("plantType") plantType: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<DiagnosisPost>

    @GET("history")
    fun getHistoryList(): Call<List<History>>
    // 서버에서 진단 결과를 가져오는 GET 요청을 정의

    @POST("history")
    @Headers("Content-Type: application/json")
    fun saveResult(@Body history: History): Call<History>

    @GET("history")
    fun getHistory(): Call<History>


}