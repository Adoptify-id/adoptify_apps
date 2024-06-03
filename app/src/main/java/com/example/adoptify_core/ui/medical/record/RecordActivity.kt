package com.example.adoptify_core.ui.medical.record

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityRecordBinding
import com.example.adoptify_core.ui.medical.MedicalRecordViewModel
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataItem
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class RecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordBinding

    private val medicalViewModel: MedicalRecordViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()

    var valueDate = ""

    private var currentUriImage: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.txtBookmark.text = resources.getString(R.string.medical_record)
        setupListener()
        recordResult()

        token = intent?.getStringExtra("token") ?: ""
        userId = intent.getIntExtra("userId", 0)
        profileViewModel.getDetailUser(token, userId)
    }

    private fun setupListener() {
        binding.apply {
            header.btnBack.setOnClickListener { onBackPressed() }
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
            val beratPet = weightPetEditText.text.toString()
            val info = addInfoEditText.text.toString()
            val clinicName = nameClinicEditText.text.toString()
            val doctorName = nameDoctorEditText.text.toString()
            val alamat = addressClinicEditText.text.toString()
            val tanggal = valueDate
            val xRay = currentUriImage?.let { uriToFile(it, this@RecordActivity).reduceImageFile() }
            val notes = medicalEditText.text.toString()

            val item = DataItem(
                kategoriPet = categoryPet,
                namePet = name,
                kesehatan = kesehatan,
                descKesehatan = descKesehatan,
                beratPet = beratPet.toInt(),
                info = info,
                klinikName = clinicName,
                dokterName = doctorName,
                alamat = alamat,
                tanggal = tanggal,
                xRay = xRay.toString(),
                catatan = notes,
                userId = userId
            )

            medicalViewModel.insertMedicalRecord(token, item)
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
                    Log.d("MedicalRecord", "error: ${it.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @SuppressLint("NewApi")
    private fun showCalendar() {
        val calendar = Calendar.getInstance()

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

}