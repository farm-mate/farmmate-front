package com.example.farmmate1

import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

interface ApiService {

    // 기기 등록
    @POST("device")
    @Headers("Content-Type: application/json")
    fun saveDeviceInfo(@Body deviceInfo: DeviceInfo): Call<Void>

    //-------------------------------------------------------------------------------

    // 식물 전체 GET 요청
    @GET("plant/device/{deviceId}")
    fun getPlantList(@Path("deviceId") deviceId: String): Call<List<PlantGet>>
    // 서버에서 식물 정보를 가져오는 GET 요청을 정의

    // 식물 POST 요청
    @Multipart
    @POST("plant")
    fun postPlant(
        @PartMap data: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part? // 이미지 파일을 MultipartBody.Part로 전송
    ): Call<PlantPost>

    // 식물 개별 GET 요청
    @GET("plant/{plantUuid}")
    fun getPlant(@Path("plantUuid") plantUuid: String?): Call<PlantGet>

    // 식물 개별 PUT 요청
    @Multipart
    @PUT("plant/{plantUuid}")
    fun editPlant(
        @Path("plantUuid") plantUuid: String,
        @PartMap data: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part? // 이미지 파일을 MultipartBody.Part로 전송
    ): Call<PlantPost>

    // 북마크 POST 요청
    @POST("plant/{plantUuid}/bookmark")
    fun postBookmark(@Path("plantUuid")plantUuid: String): Call<Void>

    // 식물 DELETE 요청
    @DELETE("plant/{plantUuid}")
    fun deletePlant(@Path("plantUuid")plantUuid: String?): Call<Void>

    // 북마크 GET 요청
    @GET("plant/device/{deviceId}/bookmark")
    fun getBookmark(@Path("deviceId")deviceId: String): Call<List<PlantGet>>

    //-------------------------------------------------------------------------------

    // 일지 post 요청
    @Multipart
    @POST("diary")
    fun postDiary(
        //@Query("plant_uuid") plantUuid: String?,
        //@Path("plantUuid") plantUuid: String,
        @PartMap data: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part? // 이미지 파일을 MultipartBody.Part로 전송
    ): Call<DiaryPost>

    // 일지 개별 GET 요청
    @GET("diary/{diaryUuid}")
    fun getDiary(@Path("diaryUuid") diaryUuid: String?): Call<DiaryGet>

    // 모든 일지 GET 요청
    @GET("diary")
    //fun getDiaryList(@Query("plant.plant_name") plantName: String?): Call<List<DiaryGet>>
    fun getDiaryList(): Call<List<DiaryGet>>

    // 일지 개별 PUT 요청
    @Multipart
    @PUT("diary/{diaryUuid}")
    fun editDiary(
        @Path("diaryUuid") diaryUuid: String,
        @PartMap data: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part? // 이미지 파일을 MultipartBody.Part로 전송
    ): Call<DiaryPost>

    // 이미지를 포함하지 않은 PUT 요청
    @Multipart
    @PUT("diary/{diaryUuid}")
    fun editDiaryWithoutImage(
        @Path("diaryUuid") diaryUuid: String,
        @PartMap data: HashMap<String, RequestBody>,
    ): Call<DiaryPost>


    // 일지 개별 DELETE 요청
    @DELETE("diary/{diaryUuid}")
    fun deleteDiary(@Path("diaryUuid")diaryUuid: String?): Call<Void>

    //-------------------------------------------------------------------------------

    // 진단 POST 요청
    @Multipart
    @POST("plant/diagnose")
    fun postDiagnosis(
        @Part("plantType") plantType: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<DiagnosisResult>

    @GET("history")
    fun getHistoryList(): Call<List<History>>
    // 서버에서 진단 결과를 가져오는 GET 요청을 정의

    @POST("history")
    @Headers("Content-Type: application/json")
    fun saveResult(@Body history: History): Call<History>

    @GET("history")
    fun getHistory(): Call<History>

    @GET("service")
    fun searchDiseases(
        @Query("apiKey") apiKey: String,
        @Query("serviceCode") serviceCode: String,
        @Query("serviceType") serviceType: String,
        @Query("cropName") cropName: String,
        @Query("sickNameKor") sickNameKor: String,
        @Query("displayCount") displayCount: Int,
        @Query("startPoint") startPoint: Int
    ): Call<SearchResponse>

    @GET("service")
    fun getDetail(
        @Query("apiKey") apiKey: String,
        @Query("serviceCode") serviceCode: String,
        @Query("sickKey") sickKey: String
    ): Call<SearchDetail>

}