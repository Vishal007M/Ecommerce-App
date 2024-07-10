package com.antsglobe.restcommerse.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val sharedPrefManager = PreferenceManager(this)
    private val splash: Long = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        if (sharedPrefManager.getMode() == true) {
            binding.splashscreen.setBackgroundColor(Color.BLACK)
            binding.bottom.setImageResource(R.drawable.splash_background_dark)
            binding.logo.setImageResource(R.drawable.logo2)
        }

        Handler().postDelayed({
            // on below line we are
            if (sharedPrefManager.isLoggedIn() == true) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                Activity().finish()
//                Activity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            } else if (sharedPrefManager.isLoggedIn() == false) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Activity().finish()
//                Activity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }, splash)
    }
}