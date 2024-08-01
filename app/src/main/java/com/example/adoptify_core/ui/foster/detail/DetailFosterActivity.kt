package com.example.adoptify_core.ui.foster.detail

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailFosterBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.foster.edit.EditFosterActivity
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.utils.ForceLogout
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class DetailFosterActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailFosterBinding

    private val adoptViewModel: AdoptViewModel by viewModel()

    private var token = ""
    private var petId by Delegates.notNull<Int>()
    private var userId by Delegates.notNull<Int>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val transitionName = intent.getStringExtra("TRANSITION_NAME")
        binding.imagePet.transitionName = transitionName

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(binding.imagePet)
            duration = 500L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(binding.imagePet)
            duration = 500L
        }

        initData()
        getDetailResult()
        setupListener()
    }

    private fun initData() {
        token = intent?.getStringExtra("TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID",0)
        petId = intent.getIntExtra("PET_ID", 0)

        adoptViewModel.getDetailPet(token, petId)
    }

    private fun setupView(data: DataAdopt?) {
        val imagePetUrl = if (data?.fotoPet == null) {
            null
        } else {
            "https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}"
        }

        val imageProfile = if (data?.fotoPet == null) {
            null
        } else {
            " https://storage.googleapis.com/bucket-adoptify/imagesUser/${data.foto}"
        }

        binding.apply {
            Glide.with(this@DetailFosterActivity)
                .load(imagePetUrl ?: R.drawable.detail_pet)
                .placeholder(R.drawable.detail_pet)
                .into(imagePet)

            namePet.text = data?.namePet?.split(" ")?.joinToString(separator = " ") { it.capitalize() }
            genderPet.text = data?.gender
            txtLocation.text = if (data?.alamat.isNullOrEmpty() || data?.provinsi.isNullOrEmpty()) "Lokasi tidak disetel" else "${data?.alamat}, ${data?.provinsi}"
            agePet.text = "${data?.umur} bulan"
            rasPet.text = data?.ras
            Glide.with(this@DetailFosterActivity)
                .load(imageProfile ?: R.drawable.ic_foster)
                .placeholder(R.drawable.ic_foster)
                .into(icFoster)
            txtDescPet.text = data?.descPet
            txtIdPet.text = "#${data?.petId}"
            txtFosterName.text = data?.fullName?.split(" ")?.joinToString(separator = " ") { it.capitalize() } ?: data?.username?.split(" ")?.joinToString { it.capitalize() }
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
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.apply {
            icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnEdit.setOnClickListener {
                val intent = Intent(this@DetailFosterActivity, EditFosterActivity::class.java)
                intent.putExtra("PET_ID", petId)
                intent.putExtra("TOKEN", token)
                intent.putExtra("USER_ID", userId)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_to_edit_pet")
                firebaseAnalytics.logEvent("navigate_to_edit_pet", bundle)
                startActivity(intent, options.toBundle())
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}