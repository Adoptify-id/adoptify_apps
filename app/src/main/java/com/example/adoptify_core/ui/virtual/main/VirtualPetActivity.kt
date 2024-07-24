package com.example.adoptify_core.ui.virtual.main

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityVirtualPetBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.adoptify_core.ui.virtual.add.AddVirtualPetActivity
import com.example.core.data.Resource
import com.example.core.domain.model.VirtualPetItem
import com.example.core.ui.VirtualPetAdapter
import com.example.core.utils.ForceLogout
import com.example.core.utils.SessionViewModel
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates


class VirtualPetActivity : BaseActivity() {

    private lateinit var binding: ActivityVirtualPetBinding

    private val virtualPetViewModel: VirtualPetViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var virtualPet: List<VirtualPetItem> = listOf()
    private var token: String? = null
    private var userId: Int? = null
    private lateinit var addVirtualPetLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVirtualPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addVirtualPetLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                observeData()
                getVirtualPet()
            }
        }
        observeData()
        getVirtualPet()
        setupListener()
    }

    private fun observeData() {
        sessionViewModel.token.observe(this) {
            token = it
            if (token != null && userId != null) {
                virtualPetViewModel.getVirtualPet(token!!, userId!!)
            }
        }
        sessionViewModel.userId.observe(this) {
            userId = it
            if (token != null && userId != null) {
                virtualPetViewModel.getVirtualPet(token!!, userId!!)
            }
        }
    }

    private fun setupListener() {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.apply {
            btnAdd.setOnClickListener {
                val intent = Intent(this@VirtualPetActivity, AddVirtualPetActivity::class.java)
                addVirtualPetLauncher.launch(intent, options)
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
                    if (virtualPet.isNotEmpty()) {
                        showContent(false)
                        showVirtualPet(virtualPet)
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    showContent(true)
                    Log.d("VirtualPetActivity", "error: ${it.message}")
                }
            }
        }
    }

    private fun showVirtualPet(data: List<VirtualPetItem>) {
        binding.apply {
            val indicator = dotsIndicator
            val viewPager: ViewPager2 = virtualPetPager
            val adapter = VirtualPetAdapter(data)
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

    private fun showContent(isShowing: Boolean) {
        binding.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE

        binding.contentNull.btnClose.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}