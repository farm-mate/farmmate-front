package com.example.farmmate1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import com.example.farmmate1.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in)
        binding.appName.visibility = View.VISIBLE
        binding.appName.startAnimation(slideInAnimation)

        // 1000ms 후에 MainActivity로 이동
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

}