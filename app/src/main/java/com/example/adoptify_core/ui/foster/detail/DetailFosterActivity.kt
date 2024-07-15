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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailFosterBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.foster.edit.EditFosterActivity
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.utils.ForceLogout
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class DetailFosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFosterBinding

    private val adoptViewModel: AdoptViewModel by viewModel()

    private var token = ""
    private var petId by Delegates.notNull<Int>()
    private var userId by Delegates.notNull<Int>()

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        getDetailResult()
        forceLogout()
        setupListener()
    }

    private fun initData() {
        token = intent?.getStringExtra("TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID",0)
        petId = intent.getIntExtra("PET_ID", 0)

        adoptViewModel.getDetailPet(token, petId)
    }

    private fun forceLogout() {
        ForceLogout.logoutLiveData.observe(this) {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        logoutDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.modal_session_expired)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //set width height card
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val btnLogin = findViewById<Button>(R.id.btnReload)
            btnLogin.backgroundTintList = ContextCompat.getColorStateList(this@DetailFosterActivity, R.color.primary_color_foster)
            btnLogin.setOnClickListener { navigateToLogin() }
            show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupView(data: DataAdopt?) {

        val imageUrl = if (data?.fotoPet == null) {
            null
        } else {
            "https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}"
        }

        binding.apply {
            Glide.with(this@DetailFosterActivity)
                .load(imageUrl ?: R.drawable.detail_pet)
                .placeholder(R.drawable.detail_pet)
                .into(imagePet)

            namePet.text = data?.namePet?.split(" ")?.joinToString(separator = " ") { it.capitalize() }
            genderPet.text = data?.gender
            agePet.text = "${data?.umur} bulan"
            rasPet.text = data?.ras
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
        binding.apply {
            icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnEdit.setOnClickListener {
                val intent = Intent(this@DetailFosterActivity, EditFosterActivity::class.java)
                intent.putExtra("PET_ID", petId)
                intent.putExtra("TOKEN", token)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        logoutDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        logoutDialog?.dismiss()
    }
}