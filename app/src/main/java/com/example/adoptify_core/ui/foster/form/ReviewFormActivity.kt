package com.example.adoptify_core.ui.foster.form

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityReviewFormBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.utils.ForceLogout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ReviewFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewFormBinding
    private var indicatorWidth: Int = 0
    private lateinit var indicatorTab: View

    private var reqId: Int? = null
    private var category: String? = null

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        forceLogout()
    }

    private fun setupView() {
        binding.apply {
            header.apply {
                btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
                btnShortcut.visibility = View.GONE
                txtBookmark.text = "Formulir"
            }
            reqId = intent.getIntExtra("REQ_ID", 0)
            category = intent.getStringExtra("CATEGORY")
            Log.d("FormDetail", "setupView: $reqId")
            val reviewAdapter = ReviewFormAdapter(this@ReviewFormActivity, reqId!!, category!!)
            val viewPager: ViewPager2 = viewForm
            viewPager.adapter = reviewAdapter
            val tabs: TabLayout = tabs
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()

            indicatorTab = indicator
            tabs.post {
                indicatorWidth = tabs.width / tabs.tabCount
                val indicatorParams = indicator.layoutParams as FrameLayout.LayoutParams
                indicatorParams.width = indicatorWidth
                indicator.layoutParams = indicatorParams
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    indicator.layoutParams =
                        (indicator.layoutParams as FrameLayout.LayoutParams).apply {
                            leftMargin = ((positionOffset + position) * indicatorWidth).toInt()
                        }
                }
            })
        }
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
            btnLogin.backgroundTintList = ContextCompat.getColorStateList(this@ReviewFormActivity, R.color.primary_color_foster)
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

    companion object {
        private val TAB_TITLES = intArrayOf(
            R.string.personal_data,
            R.string.data_general,
            R.string.data_care
        )
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