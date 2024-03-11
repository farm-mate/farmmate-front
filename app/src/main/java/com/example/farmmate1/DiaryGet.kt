package com.example.farmmate1

class DiaryGet(
    val diary_uuid: String = "",
    val diary_date: String = "",
    val plant_weather: String = "",
    val temperature: String = "",
    val humidity: String = "",
    val water_flag: Boolean = false,
    val fertilizer_flag: Boolean = false,
    val fertilizer_name: String = "",
    val fertilizer_usage: String? = null,
    val pesticide_flag: Boolean = false,
    val pesticide_name: String = "",
    val pesticide_usage: String? = null,
    val memo: String = "",
    val image_url: String = "",
    val plant: Plant? = null
)

class Plant(
    val plant_uuid: String = "",
    val plant_type: String = "",
    val plant_name: String = "",
    val plant_location: String = "",
    val memo: String = "",
    val first_planting_date: String = "",
    val image_url: String = ""
)
