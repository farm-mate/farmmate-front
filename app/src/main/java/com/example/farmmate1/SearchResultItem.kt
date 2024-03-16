package com.example.farmmate1

data class SearchResultItem(
    val cropName: String,
    val sickNameKor: String,
    val sickNameChn: String,
    val sickNameEng: String,
    val thumbImg: String,
    val oriImg: String,
    val sickKey: Int
)
