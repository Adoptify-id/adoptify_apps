package com.example.adoptify_core.ui.adopt.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailAdoptBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.adopt.process.ProcessAdoptActivity
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import org.koin.android.viewmodel.ext.android.viewModel

class DetailAdoptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAdoptBinding

    private val adoptViewModel: AdoptViewModel by viewModel()

    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        getDetailResult()
        setupListener()

    }

    private fun initData() {
        token = intent?.getStringExtra("TOKEN") ?: ""
        val petId = intent.getIntExtra("PET_ID", 0)

        adoptViewModel.getDetailPet(token, petId)
    }

    private fun setupView(data: DataAdopt?) {

        val imageUrl = if (data?.fotoPet == null) {
            null
        } else {
            "https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}"
        }

        binding.apply {
            Glide.with(this@DetailAdoptActivity)
                .load(imageUrl ?: R.drawable.detail_pet)
                .placeholder(R.drawable.detail_pet)
                .into(imagePet)

            namePet.text = data?.namePet?.split(" ")?.joinToString { it.capitalize() }
            genderPet.text = data?.gender
            agePet.text = "${data?.umur} bulan"
            rasPet.text = data?.ras
            txtDescPet.text = data?.descPet
            txtIdPet.text = "#${data?.petId}"
            txtFosterName.text = data?.fullName?.split(" ")?.joinToString { it.capitalize() } ?: data?.username?.split(" ")?.joinToString { it.capitalize() }
        }
    }

    private fun getDetailResult() {
        adoptViewModel.detail.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    Log.d("DetailAdopt", "result: ${it.data}")
                    val detailData = it.data.data?.first()
                    setupView(detailData)
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("DetailAdopt", "error: ${it.message}")
                }
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnAdopt.setOnClickListener { startActivity(Intent(this@DetailAdoptActivity, ProcessAdoptActivity::class.java)) }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}