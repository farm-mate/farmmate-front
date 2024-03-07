package com.example.farmmate1

import okhttp3.MultipartBody
import okhttp3.RequestBody

class DiagnosisPost (
    val plantType: String,
    var diagnosisImg: RequestBody
)