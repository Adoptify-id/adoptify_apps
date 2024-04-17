package com.example.adoptify_core.ui.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityLoginBinding
import com.example.adoptify_core.ui.auth.register.RegisterActivity
import com.example.adoptify_core.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        binding.btnLogin.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}