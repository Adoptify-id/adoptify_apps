package com.example.adoptify_core.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityAboutBinding
import com.google.android.material.tabs.TabLayoutMediator

class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    private val tabTitles = listOf("Aplikasi", "Pengembang")
    private val tabIcons = listOf(R.drawable.app_selector, R.drawable.developer_selector)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabLayout()
        binding.header.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupTabLayout() {
        binding.viewPager.adapter =  MenuAboutAdapter(this)
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            val customTab = LayoutInflater.from(this).inflate(R.layout.custom_bottom_navigation, null)
            val tabIcon: ImageView = customTab.findViewById(R.id.icon)
            val tabLabel: TextView = customTab.findViewById(R.id.label)
            tabIcon.setImageResource(tabIcons[position])
            tabLabel.text = tabTitles[position]

            tab.customView = customTab
        }.attach()
    }
}