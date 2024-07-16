package com.example.adoptify_core.ui.foster.form

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityReviewFormBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ReviewFormActivity : BaseActivity() {
    private lateinit var binding: ActivityReviewFormBinding
    private var indicatorWidth: Int = 0
    private lateinit var indicatorTab: View

    private var reqId: Int? = null
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
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

    companion object {
        private val TAB_TITLES = intArrayOf(
            R.string.personal_data,
            R.string.data_general,
            R.string.data_care
        )
    }
}