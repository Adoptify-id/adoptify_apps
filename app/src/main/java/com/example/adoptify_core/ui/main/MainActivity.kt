package com.example.adoptify_core.ui.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityMainBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.utils.ForceLogout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment)
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null

        forceLogout()
    }

    private fun forceLogout() {
        ForceLogout.logoutLiveData.observe(this) {
           showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        logoutDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.modal_session_expired)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //set width height card
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)
            val btnLogin = findViewById<Button>(R.id.btnReload)

            btnLogin.setOnClickListener { navigateToLogin() }
            show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        logoutDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        logoutDialog?.dismiss()
    }
}