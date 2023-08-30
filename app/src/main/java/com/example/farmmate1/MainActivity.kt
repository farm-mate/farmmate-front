package com.example.farmmate1

import HomeFragment
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.farmmate1.databinding.ActivityMainBinding
import com.example.farmmate1.databinding.FragmentDiagnosisBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.provider.Settings
import android.util.Log


class MainActivity : AppCompatActivity(), DiaryDataListener {
    override fun onDiaryDataReceived(date: Calendar, data: String) {
        // 데이터 처리 로직을 여기에 구현합니다.
    }

    private val PREFS_NAME = "MyPrefs"
    private val IS_FIRST_RUN = "isFirstRun"

//    private var mBinding: ActivityMainBinding? = null
//    private val binding get() = mBinding!!

    private lateinit var mBinding: ActivityMainBinding
    private val binding get() = mBinding!!

    private val fl : FrameLayout by lazy {
        findViewById(R.id.main_fl)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mBinding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        super.onCreate(savedInstanceState)
        setContentView(view)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean(IS_FIRST_RUN, true)

        if (isFirstRun) {
            // 처음 설치 시에만 실행되는 코드
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            Log.d("deviceid", "Device ID: $deviceId")

            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            val call = apiService.saveDiviceInfo(deviceId)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    // 성공 처리
                    if (response.isSuccessful) {
                        Log.d("divicecheck", "Saved deviceId: $deviceId")

                        // 설치 여부를 저장
                        val editor = sharedPreferences.edit()
                        editor.putBoolean(IS_FIRST_RUN, false)
                        editor.apply()
                    } else {
                        Log.e("divicecheck", "Request failed with code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("divicecheck", "Network error: ${t.message}")
                }
            })
        } else {
            Log.d("divicecheck", "App has been run before.")
        }

        val btm_nav = findViewById<BottomNavigationView>(R.id.main_btm_nav)
        btm_nav.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.btm_nav_home -> {
                        HomeFragment()
                    }

                    R.id.btm_nav_plant -> {
                        PlantFragment()
                    }

                    R.id.btm_nav_diary -> {
                        DiaryFragment()
                    }

                    R.id.btm_nav_diagnosis -> {
                        DiagnosisFragment()
                    }

                    else -> {
                        UserFragment()

                    }
                }
            )
            true
        }
        btm_nav.selectedItemId = R.id.btm_nav_home

    }
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, fragment)
            .commit()
    }
}



