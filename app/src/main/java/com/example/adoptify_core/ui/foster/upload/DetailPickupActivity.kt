package com.example.adoptify_core.ui.foster.upload

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailPickupBinding
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class DetailPickupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPickupBinding
    private val fosterViewModel: FosterViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var token: String = ""
    private var reqId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        detailSubmissionResult()
        setupView()
    }

    private fun observeData() {
        reqId = intent?.getIntExtra("REQ_ID", 0)

        sessionViewModel.token.observe(this) {
            token = it
            Log.d("DetailPickup", "token: $it , reqid: $reqId")
        }
        fosterViewModel.detailSubmissionFoster(token, reqId!!)
    }

    private fun setupView() {
        binding.apply {
            header.apply {
                txtBookmark.text = "Pengambilan"
                btnShortcut.visibility = View.GONE
                btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            }
            btnClose.setOnClickListener { finish() }
        }
    }

    private fun detailSubmissionResult() {
        fosterViewModel.detailSubmission.observe(this) {
            when(it) {
                is Resource.Loading -> {showLoading(true)}
                is Resource.Success -> {
                    showLoading(false)
                    val detail = it.data.data.first()
                    val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${detail.buktiPickup}"

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.preview_pickup_image)
                        .into(binding.imagePickup)
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("DetailPickupActivity", "error: ${it.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}