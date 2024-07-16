package com.example.adoptify_core.ui.foster

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityFosterBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.roundToInt

class FosterActivity : BaseActivity() {

    private lateinit var binding: ActivityFosterBinding
    private val tabTitles = mutableMapOf(
        "Dashboard" to R.drawable.dashboard_selector,
        "Baru" to R.drawable.add_pet_selector
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupTabLayout()
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


    fun switchTab(tabIndex: Int) {
        binding.viewPager.currentItem = tabIndex
    }
}