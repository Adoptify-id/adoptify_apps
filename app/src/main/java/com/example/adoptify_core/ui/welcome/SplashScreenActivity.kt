package com.example.adoptify_core.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.adoptify_core.R
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.foster.FosterActivity
import com.example.adoptify_core.ui.main.MainActivity
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.core.data.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class SplashScreenActivity : AppCompatActivity() {

    private val splashTime: Long = 3000
    private var token = ""
    private var roleId by Delegates.notNull<Int>()
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        loginViewModel.getSession()
        loginViewModel.getRoleId()
        checkSession()
        getRole()
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