package com.example.adoptify_core.ui.foster.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityEditProfileFosterBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataUser
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class EditProfileFosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileFosterBinding
    private val profileViewModel: ProfileViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private var currentUriImage: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
        try {
            binding.imgProfile.setImageURI(currentUriImage)
        } catch (e: Exception) {
            Log.d("AddVirtualPetActivity", "error: ${e.message.toString()}")
        }
    }

    var valueDate = ""
    private var token = ""
    private var userId by Delegates.notNull<Int>()
    private var isTokenAvailable = false
    private var isUserIdAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        setupView()
        initData()
        getToken()
        getUserId()
        updateResult()
    }

    private fun initData() {
        val gender = arrayListOf("Laki-Laki", "Perempuan")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, gender)
        binding.autoComplete.setAdapter(adapter)
        mainViewModel.getUserId()
        loginViewModel.getSession()

    }

    private fun setupView() {
        binding.apply {
            telpEditText.isEnabled = false
            emailEditText.isEnabled = false
        }
    }

    private fun getToken() {
        loginViewModel.token.observe(this) {
            when(it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    token = it.data
                    isTokenAvailable = true
                    if (isTokenAvailable && isUserIdAvailable) {
                        getDetail()
                    }
                    Log.d("EditProfile", "check: $token")
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun getUserId() {
        mainViewModel.userId.observe(this  ) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    userId = it.data
                    isUserIdAvailable = true
                    if (isTokenAvailable && isUserIdAvailable) {
                        getDetail()
                    }
                    Log.d("EditProfile", "check: $userId")
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("EditProfile", "error: ${it.message}")
                }
            }
        }
    }

    private fun getDetail() {
        profileViewModel.getDetailUser(token, userId)
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
                            Glide.with(this@EditProfileFosterActivity)
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupListener() {
        binding.apply {
            icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            dateEditText.setOnClickListener { showCalendar() }
            btnSave.setOnClickListener { updateHandler() }
            icEditProfile.setOnClickListener { galleryLauncher.launch("image/*") }
        }
    }



    private fun updateHandler() {
        binding.apply {
            val name = nameEditText.text.toString()
            val gender = autoComplete.text.toString()
            val address = addressEditText.text.toString()
            val province = provinceEditText.text.toString()
            val date = dateEditText.text.toString()
            val code = codeEditText.text.toString()
            val imageFile =
                currentUriImage?.let { uriToFile(it, this@EditProfileFosterActivity).reduceImageFile() }

            val data = DataUser(
                fullName = name,
                gender = gender,
                tglLahir = date,
                alamat = address,
                provinsi = province,
                kodePos = code,
                userId = userId,
                foto = imageFile.toString()
            )

            profileViewModel.updateProfile(token, userId, data)
        }
    }

    private fun updateResult() {
        profileViewModel.result.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    Log.d("UpdateProfile", "result: ${it.data}")
                    popUpDialog("Yeiy!", "Proses edit profil berhasil", R.drawable.alert_success)
                }
                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Proses edit profil gagal", R.drawable.alert_failed)
                    Log.d("UpdateProfile", "error: ${it.message}")
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun showCalendar() {
        val calendar = Calendar.getInstance()

        val dialogDatePicker = DatePickerDialog(
            this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                valueDate = formattedDate
                binding.dateEditText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialogDatePicker.show()
    }

    private fun popUpDialog(title: String, desc: String, image: Int) {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.alert_dialog)

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            //set width height card
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val imageView = dialog.findViewById<ImageView>(R.id.img_alert)
            val titleText = dialog.findViewById<TextView>(R.id.title_alert)
            val descText = dialog.findViewById<TextView>(R.id.desc_alert)
            val btnClose = dialog.findViewById<Button>(R.id.btnClose)

            imageView.setImageDrawable(ContextCompat.getDrawable(this@EditProfileFosterActivity, image))
            titleText.text = title
            descText.text = desc
            btnClose.setOnClickListener {
                setResult(RESULT_OK)
                dismiss()
                finish()
            }
            show()
        }
    }
}