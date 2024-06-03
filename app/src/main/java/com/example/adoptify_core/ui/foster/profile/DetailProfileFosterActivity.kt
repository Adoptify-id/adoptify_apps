package com.example.adoptify_core.ui.foster.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailProfileFosterBinding
import com.example.adoptify_core.ui.profile.EditProfileActivity
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class DetailProfileFosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProfileFosterBinding

    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()

    private lateinit var editActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileFosterBinding.inflate(layoutInflater)
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
        token = intent?.getStringExtra("TOKEN") ?: ""
        userId = intent.getIntExtra("USER_ID", 0)
        profileViewModel.getDetailUser(token, userId)

        binding.apply {
            nameEditText.isEnabled = false
            autoComplete.isEnabled = false
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
            val intent = Intent(this, EditProfileFosterActivity::class.java)
            editActivityLauncher.launch(intent)
        }
        binding.icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getDetailUser() {
        profileViewModel.detail.observe(this) {
            when(it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Log.d("ProfileActivity", "data: ${it.data}")
                    it.data.data?.map {
                        binding.apply {
                            val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesUser/${it?.foto}"
                            Log.d("Profile", "getDetailUser: ${it?.foto}")
                            nameEditText.setText(it?.fullName)
                            autoComplete.setText(it?.gender)
                            dateEditText.setText(it?.tglLahir)
                            telpEditText.setText(it?.noTelp)
                            emailEditText.setText(it?.email)
                            addressEditText.setText(it?.alamat)
                            provinceEditText.setText(it?.provinsi)
                            codeEditText.setText(it?.kodePos)
                            Glide.with(this@DetailProfileFosterActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.dummy_profile)
                                .into(imgProfile)
                        }
                    }
                }
                is Resource.Error -> {
                    Log.d("ProfileActivity", "error: ${it.message}")
                }
            }
        }
    }
}