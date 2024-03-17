package com.example.farmmate1

data class SearchDetail(
    val service: DiseaseDetailService
)

data class DiseaseDetailService(
    val chemicalPrvnbeMth: String,
    val cropName: String,
    val sickNameChn: String,
    val preventionMethod: String,
    val virusImgList: List<Image>,
    val sickNameKor: String,
    val developmentCondition: String,
    val symptoms: String,
    val etc: String,
    val virusList: List<Virus>,
    val imageList: List<Image>,
    val sickNameEng: String,
    val biologyPrvnbeMth: String
)

data class Virus(
    val virusName: String,
    val sfeNm: String
)

data class Image(
    val image: String,
    val imageTitle: String
)
