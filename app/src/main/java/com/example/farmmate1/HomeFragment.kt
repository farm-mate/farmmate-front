//package com.example.farmmate1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.farmmate1.*
import com.example.farmmate1.R
import com.example.farmmate1.component.common
import com.example.farmmate1.data.*
import com.example.farmmate1.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    companion object {
        const val API_KEY: String = "d76de2d84d0271bfaeee68d0370026f4"
        const val WEATHER_URL: String = "https://api.openweathermap.org/data/2.5/weather"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }

    private var binding: FragmentHomeBinding? = null
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var humidity:TextView
    private lateinit var wind: TextView
    private lateinit var weatherDate : TextView
    private lateinit var location: TextView
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding

//        var PlantList = arrayListOf<PlantGet>(
//            PlantGet("","","딸기",  "딸기", "2023-05-19", "재배지1", ""),
//            PlantGet("","","고추",  "고추", "2023-05-19", "재배지1", ""),
//            PlantGet("","","토마토",  "토마토", "2023-05-19", "재배지1", ""),
//            PlantGet("","","포도",  "포도", "2023-05-19", "재배지1", ""),
//            PlantGet("","","파프리카",  "파프리카", "2023-05-19", "재배지1", ""),
//            PlantGet("","","딸기5",  "딸기5", "2023-05-19", "재배지1", "")
//        )
//
//
//        val Adapter = BookmarkAdapter(requireContext(),PlantList)
//        binding?.bookmarkListLvPlants?.adapter = Adapter

        getBookmarkedPlants()

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            temperature = temperatureTv
            weatherState = weatherTv
            weatherIcon = weatherIc
            humidity = homeWeatherHumid
            wind = homeWeatherWind
            weatherDate = homeWeatherDate
            location = locationTv
        }
    }

    override fun onResume() {
        super.onResume()
        getWeatherInCurrentLocation()
    }

    private fun getWeatherInCurrentLocation() {
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationListener = LocationListener { p0 ->
            Log.d("Location", "Latitude: ${p0.latitude}, Longitude: ${p0.longitude}")
            val params: RequestParams = RequestParams()
            params.put("lat", p0.latitude)
            params.put("lon", p0.longitude)
            params.put("appid", Companion.API_KEY)
            doNetworking(params)
            getAddressFromLocation(p0.latitude, p0.longitude)

        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                WEATHER_REQUEST
            )
            return
        }
        mLocationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListener
        )
        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListener
        )
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val thoroughfare = address.thoroughfare ?: ""
                val addressText = "$thoroughfare"
                location.text = addressText
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun doNetworking(params: RequestParams) {
        val client = AsyncHttpClient()


        client.get(WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                val weatherData = WeatherData().fromJson(response)
                if (weatherData != null) {
                    updateWeather(weatherData)
                }
            }
        })
    }

    private fun updateWeather(weather: WeatherData) {
        temperature.text = weather.tempString + " ℃"
        weatherState.text = weather.weatherType
        val resourceID = resources.getIdentifier(
            weather.icon,
            "drawable",
            activity?.packageName
        )
        weatherIcon.setImageResource(resourceID)
        wind.text = "바람 "+ weather.windSpeed + "m/s " + weather.windDirection
        humidity.text= "현재습도 "+weather.humidityString + " %"
        val currentDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())
        weatherDate.text = currentDate
    }

    private fun getBookmarkedPlants() {
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val deviceId: String = sharedPreferences.getString(DEVICE_ID_KEY, "") ?: ""

        apiService.getBookmark(deviceId).enqueue(object : Callback<List<PlantGet>> {
            override fun onResponse(call: Call<List<PlantGet>>, response: Response<List<PlantGet>>) {
                // API 요청에 성공했을 때 북마크된 식물 목록을 업데이트합니다.
                val bookmarkedPlants = response.body() ?: emptyList()

                // 북마크된 식물 목록을 표시하는 어댑터를 생성하고 리스트뷰에 설정합니다.
                val adapter = BookmarkAdapter(requireContext(), bookmarkedPlants)
                binding?.bookmarkListLvPlants?.adapter = adapter
            }

            override fun onFailure(call: Call<List<PlantGet>>, t: Throwable) {
                // API 요청에 실패했을 때 에러를 로그에 출력합니다.
                Log.e("HomeFragment", "Failed to fetch bookmarked plants: ${t.message}")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener)
        }
    }
}