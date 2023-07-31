package com.example.farmmate1.data

import com.google.gson.annotations.SerializedName

class WriteDiaryData(
    /*val plantId : Int,
    val fertilizeUsage: String,
    val pesticideName: String,
    val pesticideUsage: String,
    val spinnerSelectedItem: String*/)
data class FertilizeData(
@SerializedName("fertilizeFlag") val fertilizeFlag: Boolean,
@SerializedName("fertilizeName") val fertilizeName: String,
@SerializedName("fertilizeUsage") val fertilizeUsage: String
)

data class PesticideData(
    @SerializedName("pesticideFlag") val pesticideFlag: Boolean,
    @SerializedName("pesticideName") val pesticideName: String,
    @SerializedName("pesticideUsage") val pesticideUsage: String
)

data class ContentData(
    @SerializedName("plantWeather") val plantWeather: String,
    @SerializedName("temperature") val temperature: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("waterFlag") val waterFlag: Boolean,
    @SerializedName("fertilize") val fertilize: FertilizeData,
    @SerializedName("pesticide") val pesticide: PesticideData,
    @SerializedName("memo") val memo: String
)

data class ApiData(
    @SerializedName("content") val content: ContentData
)

