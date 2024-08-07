package com.example.adoptify_core.ui.profile

import android.app.Activity
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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailProfileBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.data.Resource
import com.example.core.utils.ForceLogout
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class DetailProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailProfileBinding
    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()
    private lateinit var editActivityLauncher: ActivityResultLauncher<Intent>
    private var isErrorDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                initData()
                getDetailUser()
            }
        }
        initData()
        getDetailUser()
        setupListener()
    }

    private fun initData() {
        token = intent?.getStringExtra("token") ?: ""
        userId = intent.getIntExtra("userId", 0)
        profileViewModel.getDetailUser(token, userId)

        binding.apply {
            nameEditText.isEnabled = false
            genderEditText.isEnabled = false
            dateEditText.isEnabled = false
            telpEditText.isEnabled = false
            emailEditText.isEnabled = false
            addressEditText.isEnabled = false
            provinceEditText.isEnabled = false
            codeEditText.isEnabled = false
        }
    }

    private fun setupListener() {
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("token", token)
            intent.putExtra("userId", userId)
            editActivityLauncher.launch(intent)
        }
        binding.icArrowBack.setOnClickListener {  onBackPressedDispatcher.onBackPressed() }
    }

    private fun getDetailUser() {
        profileViewModel.detail.observe(this) {
            when(it) {
                is Resource.Loading -> {showLoading(true)}
                is Resource.Success -> {
                    showLoading(false)
                    Log.d("ProfileActivity", "data: ${it.data}")
                    it.data.data?.map {
                        binding.apply {
                            val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesUser/${it?.foto}"
                            Log.d("Profile", "getDetailUser: ${it?.foto}")
                            nameEditText.setText(it?.fullName)
                            genderEditText.setText(it?.gender)
                            dateEditText.setText(it?.tglLahir)
                            telpEditText.setText(it?.noTelp)
                            emailEditText.setText(it?.email)
                            addressEditText.setText(it?.alamat)
                            provinceEditText.setText(it?.provinsi)
                            codeEditText.setText(it?.kodePos)
                            Glide.with(this@DetailProfileActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.dummy_profile)
                                .into(imgProfile)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    if (it.message.contains("Tidak ada koneksi internet.", ignoreCase = true)) {
                        showError(it.message)
                    }
                    Log.d("ProfileActivity", "error: ${it.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        if (isErrorDialogShown) return
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.network_error_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)
            val descText = dialog.findViewById<TextView>(R.id.desc)
            val btnClose = dialog.findViewById<Button>(R.id.btnRetry)
            descText.text = message
            btnClose.setOnClickListener {
                dialog.dismiss()
                isErrorDialogShown = false
                retryFetchingData()
            }
            show()
        }
        isErrorDialogShown = true
    }

    private fun retryFetchingData() {
        showLoading(true)
        profileViewModel.getDetailUser(token, userId)
    }
}