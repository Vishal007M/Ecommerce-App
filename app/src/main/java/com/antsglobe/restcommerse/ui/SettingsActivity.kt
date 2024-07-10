package com.antsglobe.restcommerse.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.antsglobe.restcommerse.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (isDarkModeOn()) {
            // Set Onclick listener for changing to light mode
            binding!!.scDarkMode.setOnClickListener {
                changeToLightMode()
            }
        } else {
            //Toast.makeText(this, "This App is already in Light Mode.", Toast.LENGTH_SHORT).show()
        }

    }

    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

    // If the App is in Dark Mode then
    // change it to Light Mode
    fun changeToLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}