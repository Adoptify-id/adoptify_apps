package com.example.adoptify_core.ui.foster

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityFosterBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.utils.ForceLogout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.roundToInt

class FosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFosterBinding
    private val tabTitles = mutableMapOf(
        "Dashboard" to R.drawable.dashboard_selector,
        "Baru" to R.drawable.add_pet_selector
    )

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupTabLayout()
        forceLogout()
    }

    private fun setupTabLayout() {
        val title = ArrayList(tabTitles.keys)
        binding.viewPager.adapter = FosterMenuAdapter(this)
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = title[position]
        }.attach()

        tabTitles.values.forEachIndexed { index, imageResId ->
            val text = LayoutInflater.from(this).inflate(R.layout.tab_title, null) as TextView
            text.setCompoundDrawablesWithIntrinsicBounds(imageResId, 0, 0, 0)
            text.compoundDrawablePadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics
            ).roundToInt()
            binding.tabs.getTabAt(index)?.customView = text
        }
    }

    private fun forceLogout() {
        ForceLogout.logoutLiveData.observe(this) {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        if (logoutDialog == null) {
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
                btnLogin.backgroundTintList = ContextCompat.getColorStateList(
                    this@FosterActivity,
                    R.color.primary_color_foster
                )

                btnLogin.setOnClickListener { navigateToLogin() }
            }
        }
        logoutDialog?.show()
    }

    private fun dismissLogoutDialog() {
        logoutDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun switchTab(tabIndex: Int) {
        binding.viewPager.currentItem = tabIndex
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLogoutDialog()
    }

    override fun onPause() {
        super.onPause()
        dismissLogoutDialog()
    }
}