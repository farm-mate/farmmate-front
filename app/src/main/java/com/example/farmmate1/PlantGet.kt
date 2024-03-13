package com.example.farmmate1

class PlantGet (
    val deviceId: String,
    val plant_uuid: String = "",
    val plant_type: String = "",
    val plant_name: String = "",
    val first_planting_date: String = "",
    val plant_location: String = "",
    val memo:String = "",
    val image_url: String = "",

    val bookmark: Bookmark?
)
class Bookmark(
    val bookmark_uuid:String
)