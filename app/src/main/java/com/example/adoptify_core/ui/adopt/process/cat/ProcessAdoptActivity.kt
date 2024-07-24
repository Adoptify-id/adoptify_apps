package com.example.adoptify_core.ui.adopt.process.cat

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityProcessAdoptBinding
import com.example.adoptify_core.ui.adopt.SharedDataViewModel
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.utils.ForceLogout

class ProcessAdoptActivity : BaseActivity() {

    private lateinit var binding: ActivityProcessAdoptBinding
    private lateinit var sharedDataViewModel: SharedDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedDataViewModel = ViewModelProvider(this)[SharedDataViewModel::class.java]

        val adapter = PageAdoptAdapter(this)
        val viewPager: ViewPager2 = binding.processViewPager
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        val petId = intent?.getIntExtra("PET_ID", 0)
        Log.d("ProcessAdopt", "petId: $petId")
        if (petId != 0) {
            sharedDataViewModel.petId.value = petId
        }
    }
}