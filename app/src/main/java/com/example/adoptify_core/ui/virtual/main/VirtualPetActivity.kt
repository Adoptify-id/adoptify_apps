package com.example.adoptify_core.ui.virtual.main

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.databinding.ActivityVirtualPetBinding
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.adoptify_core.ui.virtual.add.AddVirtualPetActivity
import com.example.core.data.Resource
import com.example.core.domain.model.VirtualPetItem
import com.example.core.ui.VirtualPetAdapter
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates


class VirtualPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVirtualPetBinding

    private val virtualPetViewModel: VirtualPetViewModel by viewModel()

    private var virtualPet: List<VirtualPetItem> = listOf()
    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()
    private lateinit var addVirtualPetLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVirtualPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addVirtualPetLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                initData()
                getVirtualPet()
            }
        }
        initData()
        getVirtualPet()
        setupListener()

    }

    private fun initData() {
        token = intent?.getStringExtra("token") ?: ""
        userId = intent.getIntExtra("userId", 0)
        virtualPetViewModel.getVirtualPet(token, userId)
    }


    private fun setupListener() {
        binding.apply {
            btnAdd.setOnClickListener {
                val intent = Intent(this@VirtualPetActivity, AddVirtualPetActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("userId", userId)
                addVirtualPetLauncher.launch(intent)
            }
            header.icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnNext.setOnClickListener { virtualPetPager.currentItem += 1 }
            btnBack.setOnClickListener { virtualPetPager.currentItem -= 1 }
        }
    }
    private fun getVirtualPet() {
        virtualPetViewModel.virtualPet.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                    Log.d("VirtualPetActivity", "loading: true")
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("VirtualPetActivity", "data: ${it.data}")
                    virtualPet = it.data
                    binding.apply {
                        val indicator = dotsIndicator
                        val viewPager: ViewPager2 = virtualPetPager
                        val adapter = VirtualPetAdapter(it.data)
                        viewPager.isUserInputEnabled = false
                        viewPager.adapter = adapter
                        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                                btnBack.visibility = if (position == 0) View.GONE else View.VISIBLE
                            }
                        })
                        indicator.attachTo(viewPager)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("VirtualPetActivity", "error: ${it.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}