package com.example.adoptify_core.ui.coming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.adoptify_core.databinding.ActivityComingSoonBinding

class ComingSoonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComingSoonBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComingSoonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}