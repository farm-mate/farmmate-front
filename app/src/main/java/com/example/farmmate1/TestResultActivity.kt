package com.example.farmmate1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.farmmate1.databinding.ActivityMainBinding

class TestResultActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}