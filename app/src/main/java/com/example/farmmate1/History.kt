package com.example.farmmate1

data class History(
    val plantDiseaseUuid: String,
    val disease: Disease,
    val created_at: String
)

data class Disease(
    val diseaseUuid: String,
    val plantName: String,
    val diseaseName: String,
    val diseaseCode: Int?,
    val diseaseSymptom: String?,
    val diseaseCause: String?,
    val diseaseTreatment: String?,
    val diagnosisCode: Int?,
    val plantImg: String? // Assuming plantImg is of type String
)
