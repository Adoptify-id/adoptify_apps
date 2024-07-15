package com.example.adoptify_core.ui.foster.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailProfileFosterBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import com.example.core.utils.ForceLogout
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class DetailProfileFosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProfileFosterBinding

    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()
    private var logoutDialog: Dialog? = null

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
        forceLogout()
        setupListener()
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
            btnLogin.backgroundTintList = ContextCompat.getColorStateList(this@DetailProfileFosterActivity, R.color.primary_color_foster)
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

    override fun onDestroy() {
        super.onDestroy()
        logoutDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        logoutDialog?.dismiss()
    }
}