package com.example.adoptify_core

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.utils.ForceLogout

open class BaseActivity : AppCompatActivity() {

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ForceLogout.resetSessionExpired()
        ForceLogout.sessionExpired.observe(this) { sessionExpired ->
            if (sessionExpired) { showLogoutDialog() }
        }
    }

    private fun showLogoutDialog() {
        logoutDialog?.dismiss()
        logoutDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.modal_session_expired)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        startActivity(intent, options.toBundle())
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