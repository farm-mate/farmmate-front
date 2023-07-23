import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import retrofit2.Response
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
    private lateinit var weatherTip: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var humidity:TextView

    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            temperature = temperatureTv
            weatherState = weatherTv
            //weatherTip = weatherTipTv
            weatherIcon = weatherIc
            humidity = homeFragmentWeatherClHumid
        }
    }

    override fun onResume() {
        super.onResume()
        getWeatherInCurrentLocation()
    }

    private fun getWeatherInCurrentLocation() {
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationListener = LocationListener { p0 ->
            val params: RequestParams = RequestParams()
            params.put("lat", p0.latitude)
            params.put("lon", p0.longitude)
            params.put("appid", Companion.API_KEY)
            doNetworking(params)
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
        humidity.text= "습도 :"+weather.humidityString + " %"
        weatherIcon.setImageResource(resourceID)
    }

    override fun onPause() {
        super.onPause()
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener)
        }
    }
}

//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import com.example.farmmate1.R
//import com.example.farmmate1.component.common
//import com.example.farmmate1.data.ITEM
//import com.example.farmmate1.data.ModelWeather
//import com.example.farmmate1.data.WEATHER
//import com.example.farmmate1.data.WeatherObject
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import retrofit2.Call
//import retrofit2.Response
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//
//class HomeFragment : Fragment() {
//
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var rootView: View
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//
//    ): View? {
//        // 프래그먼트의 레이아웃을 인플레이션합니다.
//
//        return inflater.inflate(R.layout.fragment_home, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // fusedLocationClient 초기화
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//
//        // 위치 권한 요청
//        val permissionList = arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            permissionList,
//            LOCATION_PERMISSION_REQUEST_CODE
//        )
//
//        // 위치 요청 및 날씨 정보 가져오기
//        requestLocation()
//    }
//
//    private fun requestLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                location?.let {
//                    val curPoint = common().dfsXyConv(it.latitude, it.longitude)
//                    val baseDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
//                    val timeH = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
//                    val timeM = SimpleDateFormat("mm", Locale.getDefault()).format(Date())
//                    val baseTime = common().getBaseTime(timeH, timeM)
//
//                    // nx와 ny 좌표를 사용하여 날씨 정보 가져오는 메소드 호출
//                    setWeather(curPoint.x, curPoint.y, baseDate, baseTime)
//                }
//            }.addOnFailureListener { e ->
//                Log.e(TAG, "위치 가져오기 실패: ${e.message}")
//            }
//        } else {
//            Log.e(TAG, "위치 권한이 허용되지 않았습니다.")
//        }
//    }
//
//    private fun setWeather(nx: Int, ny: Int, baseDate: String, baseTime: String) {
//        // API 호출하고 응답 처리
//        val call =
//            WeatherObject.getRetrofitService().getWeather(60, 1, "JSON", baseDate, baseTime, nx, ny)
//        call.enqueue(object : retrofit2.Callback<WEATHER> {
//            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
//                if (response.isSuccessful) {
//                    val items = response.body()?.response?.body?.items?.item
//
//                    items?.let { weatherItems ->
//                        if (weatherItems.isNotEmpty()) {
//                            val weatherData = parseWeatherData(weatherItems)
//
//                            // 날씨 정보로 UI 업데이트
//                            displayWeatherInfo(weatherData)
//                        } else {
//                            Log.e(TAG, "날씨 정보가 없습니다.")
//                        }
//                    }
//                } else {
//                    Log.e(TAG, "API 호출 실패: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
//                Log.e(TAG, "API 호출 실패: ${t.message}")
//            }
//        })
//    }
//
//    private fun parseWeatherData(weatherItems: List<ITEM>): ModelWeather {
//        val weatherData = ModelWeather()
//
//        for (item in weatherItems) {
//            when (item.category) {
//                "POP" -> weatherData.rainType = item.fcstValue
//                "REH" -> weatherData.humidity = item.fcstValue
//                "SKY" -> weatherData.sky = item.fcstValue
//                "T3H" -> weatherData.temp = item.fcstValue
//                "TMN" -> weatherData.minTemp = item.fcstValue
//                "TMX" -> weatherData.maxTemp = item.fcstValue
//            }
//        }
//
//        return weatherData
//    }
//
//    private fun displayWeatherInfo(weatherData: ModelWeather) {
//        val sky = weatherData.sky
//        val rainType = weatherData.rainType
//
//        val weatherImageRes = getRainImage(rainType, sky)
//        val weatherImageView: ImageView =
//            requireView().findViewById(R.id.weather_icon_im_homefragment)
//        weatherImageView.setImageResource(weatherImageRes)
//
//        // 현재 하늘상태, 온도, 최고기온, 최저기온 등의 정보를 표시하는 TextView들이 있다고 가정하고, 해당 TextView들에 값을 설정
//        val temperatureTextView: TextView =
//            requireView().findViewById(R.id.home_fragment_weather_cl_tempe)
//        temperatureTextView.text = "${weatherData.temp}°C"
//
//        val maxTemperatureTextView: TextView =
//            requireView().findViewById(R.id.home_fragment_weather_cl_max_tempe)
//        maxTemperatureTextView.text = "${weatherData.maxTemp}°C"
//
//        val minTemperatureTextView: TextView =
//            requireView().findViewById(R.id.home_fragment_weather_cl_min_tempe)
//        minTemperatureTextView.text = "${weatherData.minTemp}°C"
//    }
//
//    private fun getRainImage(rainType: String, sky: String): Int {
//        return when (rainType) {
//            "0" -> getWeatherImage(sky)
//            "1" -> R.drawable.rainy
//            else -> getWeatherImage(sky)
//        }
//    }
//
//    private fun getWeatherImage(sky: String): Int {
//        return when (sky) {
//            "1" -> R.drawable.sunny
//            "3" -> R.drawable.cloudy
//            "4" -> R.drawable.cloudy
//            else -> R.drawable.ic_launcher_foreground
//        }
//    }
//
//    companion object {
//        private const val TAG = "WeatherFragment"
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//    }
//}
