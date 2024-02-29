package com.example.farmmate1

data class Diary(
    val plantName: String,
    val diaryDate: String,
    val plantWeather: String,
    val temperature: Int,
    val humidity: Int,
    val waterFlag: Boolean,
    val fertilizeFlag: Boolean,
    val fertilizeName: String,
    val fertilizeUsage: String,
    val pesticideFlag: Boolean,
    val pesticideName: String,
    val pesticideUsage: String,
    val memo: String
)
