package com.example.adoptify_core.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivitySplashScreenBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.foster.FosterActivity
import com.example.adoptify_core.ui.main.MainActivity
import com.example.core.data.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val splashTime: Long = 3000
    private var token = ""
    private var roleId by Delegates.notNull<Int>()
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.getSession()
        loginViewModel.getRoleId()
        checkSession()
        getRole()

        val fadeInScale = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
        binding.logo.startAnimation(fadeInScale)

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            checkToken()
        }, splashTime)
    }

    private fun checkToken() {
        Log.d("SplashScreen", "checkToken: $token")
        if (token.isEmpty()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        } else {
            if (roleId == 1) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, FosterActivity::class.java))
                finish()
            }

        }
    }

    private fun checkSession() {
        loginViewModel.token.observe(this) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Log.d("SplashScreen", "success: ${it.data}")
                    token = it.data
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun getRole() {
        loginViewModel.roleId.observe(this) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    roleId = it.data
                    Log.d("SplashScreen", "role id: ${it.data}")
                }

                is Resource.Error -> {
                    Log.d("SplashScreen", "error : ${it.message}")
                }
            }
        }
    }

}