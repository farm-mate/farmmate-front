package com.example.farmmate1

import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PlantPost (
    val plantType: String,
    val plantName: String,
    val firstPlantingDate: String,
    val plantLocation: String,
    val memo: String,
    val deviceId: String,
    var plantImg: MultipartBody.Part? = null
)
