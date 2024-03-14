package com.example.farmmate1

import okhttp3.MultipartBody

class DiaryPost (
    val plantName: String,
    val plantUuid: String? = "",
    val diaryDate: String,
    val plantWeather: String,
    val temperature: String,
    val humidity: String,
    val waterFlag: Boolean,
    val fertilizerFlag: Boolean,
    val fertilizerName: String,
    val fertilizerUsage: String,
    val pesticideFlag: Boolean,
    val pesticideName: String,
    val pesticideUsage: String,
    val memo: String,
    var image: MultipartBody.Part? = null
        )