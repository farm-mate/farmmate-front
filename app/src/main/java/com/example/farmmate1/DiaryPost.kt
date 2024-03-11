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
    val fertilizeFlag: Boolean,
    val fertilizeName: String,
    val fertilizeUsage: String,
    val pesticideFlag: Boolean,
    val pesticideName: String,
    val pesticideUsage: String,
    val memo: String,
    var image: MultipartBody.Part? = null
        )