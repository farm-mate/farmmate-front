package com.example.farmmate1.data

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class WeatherData {
    lateinit var tempString: String
    lateinit var icon: String
    lateinit var mintempString:String
    lateinit var maxtempString:String
    lateinit var weatherType: String
    private var weatherId: Int = 0
    private var tempInt: Int =0
    lateinit var humidityString:String
    lateinit var windSpeed:String
    lateinit var windDirection:String

    fun fromJson(jsonObject: JSONObject?): WeatherData? {
        try{
            Log.d("WeatherData", "JSON Object: $jsonObject")
            var weatherData = WeatherData()
            weatherData.weatherId = jsonObject?.getJSONArray("weather")?.getJSONObject(0)?.getInt("id")!!
            weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
            weatherData.icon = updateWeatherIcon(weatherData.weatherId)

            val roundedTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp")-273.15).toInt()
            weatherData.tempString = roundedTemp.toString()
            weatherData.tempInt = roundedTemp

            val windSpeed: Double = jsonObject.getJSONObject("wind").getDouble("speed")
            weatherData.windSpeed = windSpeed.toString()

            val windDirection: Double = jsonObject.getJSONObject("wind").getDouble("deg")
            weatherData.windDirection = updateWindDirection(windDirection.toInt())

            val roundedHumidity: Int = (jsonObject.getJSONObject("main").getDouble("humidity")).toInt()
            weatherData.humidityString = roundedHumidity.toString()
            return weatherData
        }catch (e: JSONException){
            e.printStackTrace()
            return null
        }
    }

    private fun updateWeatherIcon(condition: Int): String {
        if (condition in 200..299) {
            return "thunderstorm"
        } else if (condition in 300..499) {
            return "lightrain"
        } else if (condition in 500..599) {
            return "rain"
        } else if (condition in 600..700) {
            return "snow"
        } else if (condition in 701..771) {
            return "fog"
        } else if (condition in 772..799) {
            return "overcast"
        } else if (condition == 800) {
            return "clear"
        } else if (condition in 801..804) {
            return "cloudy"
        } else if (condition in 900..902) {
            return "thunderstorm"
        }
        if (condition == 903) {
            return "snow"
        }
        if (condition == 904) {
            return "clear"
        }
        return if (condition in 905..1000) {
            "thunderstorm"
        } else "dunno"
    }

    private fun updateWindDirection(degrees: Int): String {
        return when (degrees) {
            in 0..45 -> "북풍"
            in 46..90 -> "북동풍"
            in 91..135 -> "동풍"
            in 136..180 -> "남동풍"
            in 181..225 -> "남풍"
            in 226..270 -> "남서풍"
            in 271..315 -> "서풍"
            in 316..360 -> "북서풍"
            else -> "알 수 없음"
        }
    }
}