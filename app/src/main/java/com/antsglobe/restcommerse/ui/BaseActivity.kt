package com.antsglobe.restcommerse.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antsglobe.restcommerse.databinding.ActivityMainBinding

class BaseActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.buttonSplash.setOnClickListener {
            val i = Intent(this@BaseActivity, LoginActivity::class.java)
            startActivity(i)
            finish()
        }


    }

}