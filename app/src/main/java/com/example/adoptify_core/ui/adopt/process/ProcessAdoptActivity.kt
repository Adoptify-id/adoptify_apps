package com.example.adoptify_core.ui.adopt.process

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityProcessAdoptBinding
import com.example.adoptify_core.ui.adopt.PageAdoptAdapter

class ProcessAdoptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProcessAdoptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PageAdoptAdapter(this)
        val viewPager: ViewPager2 = binding.processViewPager
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
    }
}