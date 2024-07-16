package com.example.adoptify_core.ui.medical.record

import android.annotation.SuppressLint
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
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityRecordBinding
import com.example.adoptify_core.ui.medical.MedicalRecordViewModel
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataItem
import com.example.core.utils.SessionViewModel
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.Q)
class RecordActivity : BaseActivity() {

    private lateinit var binding: ActivityRecordBinding

    private val medicalViewModel: MedicalRecordViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var token: String? = null
    private var userId: Int? = null

    var valueDate = ""

    private var progressDialog: Dialog? = null

    private var currentUriImage: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.txtBookmark.text = resources.getString(R.string.medical_record)
        setupView()
        observeData()
        setupListener()
        recordResult()
        validateForm()
    }

    private fun setupView() {
        binding.apply {
            namePetEditText.addTextChangedListener(textWatcher)
            conditionPetEditText.addTextChangedListener(textWatcher)
            descPetEditText.addTextChangedListener(textWatcher)
            weightPetEditText.addTextChangedListener(textWatcher)
            addInfoEditText.addTextChangedListener(textWatcher)
            nameClinicEditText.addTextChangedListener(textWatcher)
            nameDoctorEditText.addTextChangedListener(textWatcher)
            addInfoEditText.addTextChangedListener(textWatcher)
            addressClinicEditText.addTextChangedListener(textWatcher)
            medicalEditText.addTextChangedListener(textWatcher)

            radioCategory.setOnCheckedChangeListener { _, _ -> validateForm() }
        }
    }

    private fun observeData() {
        sessionViewModel.token.observe(this) {
            token = it
            if (token != null && userId != null) {
                profileViewModel.getDetailUser(token!!, userId!!)
            }
        }

        sessionViewModel.userId.observe(this) {
            userId = it
            if (token != null && userId != null) {
                profileViewModel.getDetailUser(token!!, userId!!)
            }
        }

    }

    private fun setupListener() {
        binding.apply {
            header.btnBack.setOnClickListener {  onBackPressedDispatcher.onBackPressed() }
            btnSave.setOnClickListener { recordHandler() }
            btnDate.setOnClickListener { showCalendar() }
            btnXray.setOnClickListener { galleryLauncher.launch("image/*") }
            switchButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getProfileUser()
                } else {
                    clearEditText()
                }
            }
        }
    }

    private fun getProfileUser() {
        profileViewModel.detail.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("RecordActivity", "data: ${it.data}")

                    if (binding.switchButton.isChecked) {
                        it.data.data?.map { profile ->
                            binding.usernameEditText.setText(profile?.fullName)
                            binding.telephoneEditText.setText(profile?.noTelp)
                            binding.emailEditText.setText(profile?.email)
                            Log.d(
                                "RecordActivity",
                                "username: ${profile?.username} ${profile?.noTelp}  ${profile?.email}"
                            )
                        }
                    }

                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("RecordActivity", "error: ${it.message}")
                }
            }
        }
    }

    private fun recordHandler() {
        binding.apply {
            val categoryPet =
                radioCategory.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = binding.root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: ""

            val name = namePetEditText.text.toString()
            val kesehatan = conditionPetEditText.text.toString()
            val descKesehatan = descPetEditText.text.toString()
            val beratPet = weightPetEditText.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
            val info = addInfoEditText.text.toString()
            val clinicName = nameClinicEditText.text.toString()
            val doctorName = nameDoctorEditText.text.toString()
            val alamat = addressClinicEditText.text.toString()
            val tanggal = valueDate
            val xRay = currentUriImage?.let { uriToFile(it, this@RecordActivity).reduceImageFile() }?.path
            val notes = medicalEditText.text.toString()

            val item = DataItem(
                kategoriPet = categoryPet,
                namePet = name,
                kesehatan = kesehatan,
                descKesehatan = descKesehatan,
                beratPet = beratPet,
                info = info,
                klinikName = clinicName,
                dokterName = doctorName,
                alamat = alamat,
                tanggal = tanggal,
                xRay = xRay,
                catatan = notes,
                userId = userId
            )

            medicalViewModel.insertMedicalRecord(token!!, item)
        }
    }

    private fun recordResult() {
        medicalViewModel.resultInsert.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("MedicalRecord", "recordResult: ${it.data}")
                    setResult(RESULT_OK)
                    finish()
                }

                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Penambahan data medical gagal", it.message, R.drawable.alert_failed)
                    Log.d("MedicalRecord", "error: ${it.message}")
                }
            }
        }
    }

    private fun validateForm() {
        binding.apply {
            val isRadioGroupCategorySelected = radioCategory.checkedRadioButtonId != -1
            val name = namePetEditText.text.toString()
            val kesehatan = conditionPetEditText.text.toString()
            val descKesehatan = descPetEditText.text.toString()
            val beratPet = weightPetEditText.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
            val info = addInfoEditText.text.toString()
            val clinicName = nameClinicEditText.text.toString()
            val doctorName = nameDoctorEditText.text.toString()
            val alamat = addressClinicEditText.text.toString()
            val tanggal = valueDate
            val xRay = currentUriImage?.let { uriToFile(it, this@RecordActivity).reduceImageFile() }?.path
            val notes = medicalEditText.text.toString()

            val isFormValid = name.isNotEmpty() && kesehatan.isNotEmpty() && descKesehatan.isNotEmpty() && info.isNotEmpty() && clinicName.isNotEmpty() && doctorName.isNotEmpty() && alamat.isNotEmpty() && tanggal.isNotEmpty() && xRay!!.isNotEmpty() && notes.isNotEmpty() && isRadioGroupCategorySelected && beratPet > 0
            btnSave.isEnabled = isFormValid
            btnSave.backgroundTintList = ContextCompat.getColorStateList(
                this@RecordActivity,
                if (isFormValid) R.color.pink else R.color.btn_disabled
            )

        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {validateForm()}
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBarDialog() else dismissProgressDialog()
    }

    @SuppressLint("NewApi")
    private fun showCalendar() {
        val calendar = Calendar.getInstance()

        if (valueDate.isNotEmpty()) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            calendar.time = dateFormat.parse(valueDate)
        }

        val dialogDatePicker = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                valueDate = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialogDatePicker.show()
    }

    private fun clearEditText() {
        binding.usernameEditText.text?.clear()
        binding.telephoneEditText.text?.clear()
        binding.emailEditText.text?.clear()
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(this).apply {
                val view = LayoutInflater.from(this@RecordActivity).inflate(R.layout.dialog_progress, null)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun popUpDialog(title: String, desc: String, subDesc: String, image: Int) {
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
            val subDescText = dialog.findViewById<TextView>(R.id.sub_desc_alert)
            val btnClose = dialog.findViewById<Button>(R.id.btnClose)

            imageView.setImageDrawable(ContextCompat.getDrawable(this@RecordActivity, image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener { dismiss() }
            show()
        }
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }

    override fun onPause() {
        dismissProgressDialog()
        super.onPause()
    }
}