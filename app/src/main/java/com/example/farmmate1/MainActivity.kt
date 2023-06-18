package com.example.farmmate1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.farmmate1.databinding.ActivityMainBinding
import com.example.farmmate1.databinding.FragmentDiagnosisBinding
import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : AppCompatActivity() {

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



