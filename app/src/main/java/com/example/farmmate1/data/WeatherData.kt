package com.example.farmmate1.data

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
    var humidityInt:Int =0
    private var mintempInt : Int=0
    private var maxtempInt : Int=0


    fun fromJson(jsonObject: JSONObject?): WeatherData? {
        try{
            var weatherData = WeatherData()
            weatherData.weatherId = jsonObject?.getJSONArray("weather")?.getJSONObject(0)?.getInt("id")!!
            weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
            weatherData.icon = updateWeatherIcon(weatherData.weatherId)
            val roundedTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp")-273.15).toInt()
            weatherData.tempString = roundedTemp.toString()
            weatherData.tempInt = roundedTemp

            val roundedminTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp_min")-273.15).toInt()
            weatherData.mintempString = roundedminTemp.toString()
            weatherData.mintempInt = roundedminTemp

            val roundedmaxTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp_max")-273.15).toInt()
            weatherData.maxtempString = roundedmaxTemp.toString()
            weatherData.mintempInt = roundedminTemp

            val roundedHumidity: Int = (jsonObject.getJSONObject("main").getDouble("humidity")).toInt()
            weatherData.humidityString = roundedHumidity.toString()
            return weatherData
        }catch (e: JSONException){
            e.printStackTrace()
            return null
        }
    }

    fun updateData(jsonObject: JSONObject?) {
        try {
            weatherId = jsonObject?.getJSONArray("weather")?.getJSONObject(0)?.getInt("id")!!
            weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
            icon = updateWeatherIcon(weatherId)
            val roundedTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp") - 273.15).toInt()
            tempString = roundedTemp.toString()
            tempInt = roundedTemp

            val roundedminTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp_min") - 273.15).toInt()
            mintempString = roundedminTemp.toString()
            mintempInt = roundedminTemp


            val roundedmaxTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp_max") - 273.15).toInt()
            maxtempString = roundedmaxTemp.toString()
            maxtempInt = roundedmaxTemp
            
        } catch (e: JSONException) {
            e.printStackTrace()
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
}