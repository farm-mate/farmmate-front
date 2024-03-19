package com.example.farmmate1.api

data class SearchResponse(
    val service: Service
)

data class Service(
    val buildTime: String,
    val totalCount: Int,
    val startPoint: Int,
    val displayCount: Int,
    val list: List<Disease>
)

data class Disease(
    val cropName: String,
    val sickNameChn: String,
    val sickNameKor: String,
    val sickNameEng: String,
    val thumbImg: String,
    val oriImg: String,
    val sickKey: String
)
