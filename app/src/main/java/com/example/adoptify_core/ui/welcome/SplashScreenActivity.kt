package com.example.adoptify_core.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.adoptify_core.R

class SplashScreenActivity : AppCompatActivity() {

    private val splashTime: Long = 3000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            startActivity(Intent(this, WelcomeActivity::class.java))
        }, splashTime)

    }
}