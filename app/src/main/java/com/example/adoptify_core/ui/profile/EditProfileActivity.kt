package com.example.adoptify_core.ui.profile

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityEditProfileBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataUser
import com.example.core.utils.convertUrlToFile
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val profileViewModel: ProfileViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private var currentUriImage: Uri? = null

    private var lastUpdatedData = DataUser()

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            currentUriImage = uri
            try {
                binding.imgProfile.setImageURI(currentUriImage)
                validateForm()
            } catch (e: Exception) {
                Log.d("EditProfileActivity", "error: ${e.message.toString()}")
            }
        }
    }

    var valueDate = ""
    private var token = ""
    private var userId by Delegates.notNull<Int>()
    private var isTokenAvailable = false
    private var isUserIdAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        setupView()
        initData()
        validateForm()
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
            nameEditText.addTextChangedListener(textWatcher)
            autoComplete.addTextChangedListener(textWatcher)
            dateEditText.addTextChangedListener(textWatcher)
            addressEditText.addTextChangedListener(textWatcher)
            provinceEditText.addTextChangedListener(textWatcher)
            codeEditText.addTextChangedListener(textWatcher)
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
                            nameEditText.setText(it?.fullName)
                            autoComplete.setText(it?.gender)
                            dateEditText.setText(it?.tglLahir)
                            telpEditText.setText(it?.noTelp)
                            emailEditText.setText(it?.email)
                            addressEditText.setText(it?.alamat)
                            provinceEditText.setText(it?.provinsi)
                            codeEditText.setText(it?.kodePos)
                            Glide.with(this@EditProfileActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.dummy_profile)
                                .into(imgProfile)

                            lastUpdatedData = DataUser(
                                fullName = it?.fullName,
                                gender = it?.gender,
                                tglLahir = it?.tglLahir,
                                alamat = it?.alamat,
                                provinsi = it?.provinsi,
                                kodePos = it?.kodePos,
                                userId = userId,
                                foto = it?.foto
                            )
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
            lifecycleScope.launch {
                val imageFile = if (currentUriImage != null) {
                    uriToFile(currentUriImage!!, this@EditProfileActivity).reduceImageFile().path
                } else {
                    lastUpdatedData.foto?.let {
                        val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesUser/$it"
                        convertUrlToFile(this@EditProfileActivity,imageUrl)?.path ?: it
                    }
                }
                val finalImageFile = imageFile ?: ""
                val data = DataUser(
                    fullName = name,
                    gender = gender,
                    tglLahir = date,
                    alamat = address,
                    provinsi = province,
                    kodePos = code,
                    userId = userId,
                    foto = finalImageFile
                )
                profileViewModel.updateProfile(token, userId, data)
            }
        }
    }


    private fun validateForm() {
        binding.apply {
            val name = nameEditText.text.toString()
            val gender = autoComplete.text.toString()
            val date = dateEditText.text.toString()
            val address = addressEditText.text.toString()
            val province = provinceEditText.text.toString()
            val code = codeEditText.text.toString()

            val isNameChanged = name != lastUpdatedData.fullName && name.isNotEmpty()
            val isGenderChanged = gender != lastUpdatedData.gender && gender.isNotEmpty()
            val isDateChanged = date != lastUpdatedData.tglLahir && date.isNotEmpty()
            val isAddressChanged = address != lastUpdatedData.alamat && address.isNotEmpty()
            val isProvinceChanged = province != lastUpdatedData.provinsi && province.isNotEmpty()
            val isCodeChanged = code != lastUpdatedData.kodePos && code.isNotEmpty()
            val isImageChanged = currentUriImage != null

            val isFormValid = isImageChanged || isNameChanged || isGenderChanged || isDateChanged || isAddressChanged || isProvinceChanged || isCodeChanged
            btnSave.isEnabled = isFormValid
            btnSave.backgroundTintList = ContextCompat.getColorStateList(this@EditProfileActivity, if (isFormValid) R.color.primaryColor else R.color.btn_disabled)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { validateForm() }
    }

    private fun updateResult() {
        profileViewModel.result.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    Log.d("UpdateProfile", "result: ${it.data}")
                    popUpDialog("Yeiy!", "Pengeditan profil berhasil", "Selamat! Profil Anda telah berhasil diperbarui. Perubahan yang Anda lakukan telah disimpan dengan sukses",R.drawable.alert_success)
                }
                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Pengeditan profil gagal", it.message,R.drawable.alert_failed)
                    Log.d("UpdateProfile", "error: ${it.message}")
                }
            }
        }
    }

    private fun showCalendar() {
        val calendar = Calendar.getInstance()

        if (valueDate.isNotEmpty()) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            calendar.time = dateFormat.parse(valueDate) ?: Date()
        }

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

    private fun popUpDialog(title: String, desc: String, subDesc: String ,image: Int) {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.alert_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val imageView = dialog.findViewById<ImageView>(R.id.img_alert)
            val titleText = dialog.findViewById<TextView>(R.id.title_alert)
            val descText = dialog.findViewById<TextView>(R.id.desc_alert)
            val subDescText = dialog.findViewById<TextView>(R.id.sub_desc_alert)
            val btnClose = dialog.findViewById<Button>(R.id.btnClose)

            imageView.setImageDrawable(ContextCompat.getDrawable(this@EditProfileActivity, image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener {
                setResult(RESULT_OK)
                dismiss()
                finish()
            }
            show()
        }
    }
}