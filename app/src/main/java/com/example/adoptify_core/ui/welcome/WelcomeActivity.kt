package com.example.adoptify_core.ui.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val indicator = binding.dotsIndicator
        val adapter = IndicatorAdapter(this)
        val viewPager: ViewPager2 = binding.welcomeViewPager
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        indicator.dotsClickable = false
        indicator.attachTo(viewPager)
        viewPager.setPageTransformer(DepthPageTransformer())

    }
}