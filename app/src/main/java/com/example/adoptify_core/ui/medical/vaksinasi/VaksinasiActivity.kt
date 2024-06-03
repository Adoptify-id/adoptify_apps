package com.example.adoptify_core.ui.medical.vaksinasi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityVaksinasiBinding
import com.example.adoptify_core.ui.medical.MedicalRecordActivity
import com.example.adoptify_core.ui.medical.MedicalRecordViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.VaksinasiItem
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class VaksinasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVaksinasiBinding
    private val medicalViewModel: MedicalRecordViewModel by viewModel()
    var valueDate = ""
    var titleDialog = ""
    var petVaksin = mutableListOf("Distemper", "Parvovirus", "Hepatitis", "Rabies", "Leptospira", "Parainfluenza", "Bordetella")
    var vaksinSelected = ""
    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaksinasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.txtBookmark.text = resources.getString(R.string.vaksinasi)
        setupListener()
        vaksinasiResult()
    }

    private fun setupListener() {
        binding.apply {
            header.btnBack.setOnClickListener { onBackPressed() }
            btnDate.setOnClickListener { showCalendar() }
            btnVaksin.setOnClickListener { showDialog() }
            btnSave.setOnClickListener { vaksinasiHandler() }
            radioCat.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    titleDialog = resources.getString(R.string.vaksin_kucing)
                    petVaksin = mutableListOf("Feline viral rhinotracheitis", "Feline calicivirus", "Feline panleukopenia", "Rabies", "Feline chlamydiosis")
                }
            }

            radioDog.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    titleDialog = resources.getString(R.string.vaksin_anjing)
                    petVaksin = mutableListOf("Distemper", "Parvovirus", "Hepatitis", "Rabies", "Leptospira", "Parainfluenza", "Bordetella")
                }
            }
        }
    }

    private fun vaksinasiHandler() {
        token = intent?.getStringExtra("token") ?: ""
        Log.d("InsertVaksinasi", "token: $token")
        userId = intent.getIntExtra("userId",0)
        binding.apply {
            val categoryPet = radioCategory.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                val radioButton = binding.root.findViewById<RadioButton>(radioButtonId)
                radioButton.text.toString()
            } ?: ""

            val name = namePetEditText.text.toString()
            val kesehatan = conditionPetEditText.text.toString()
            val descKesehatan = descPetEditText.text.toString()
            val beratPet = weightPetEditText.text.toString()
            val info = addInfoEditText.text.toString()
            val klinikName = nameClinicEditText.text.toString()
            val doctorName = nameDoctorEditText.text.toString()
            val alamat = addressClinicEditText.text.toString()
            val tanggal = valueDate
            val jenisVaksin = vaksinSelected
            val catatan = medicalEditText.text.toString()

            val data = VaksinasiItem(
                kategoriPet = categoryPet,
                name = name,
                kesehatan = kesehatan,
                descKesehatan = descKesehatan,
                beratPet = beratPet.toInt(),
                info = info,
                klinikName = klinikName,
                dokterName = doctorName,
                alamat = alamat,
                tanggal = tanggal,
                jenisVaksin = jenisVaksin,
                catatan = catatan,
                userId = userId
            )
            medicalViewModel.insertVaksinasi(token, data)
        }
    }

    private fun vaksinasiResult() {
        medicalViewModel.result.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    Log.d("InsertVaksinasi", "result: ${it.data}")
                    setResult(RESULT_OK)
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("InsertVaksinasi", "error: ${it.message}")
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
            this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
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

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.dialog_vaksin)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            //set width height card
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val radio_group = dialog.findViewById<RadioGroup>(R.id.radio_group_anjing)
            val title = dialog.findViewById<TextView>(R.id.txtVaksin)
            val button = dialog.findViewById<Button>(R.id.btnSave)
            title.text = titleDialog
            mutableRadioButton(radio_group, petVaksin)

            button.setOnClickListener {
                val selectedRadioButtonId =  radio_group.checkedRadioButtonId
                if (selectedRadioButtonId != -1 ) {
                    val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                    vaksinSelected = selectedRadioButton.text.toString()
                }
                dismiss()
            }
            show()
        }
    }

    //add item radioButton
    private fun mutableRadioButton(radioGroup: RadioGroup, items: List<String>) {
        radioGroup.removeAllViews()
        for (vaksin in items) {
            val radioButton = RadioButton(this).apply {
                layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelSize(R.dimen.radio_button_height)
                ).apply {
                    setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.radio_button_margin_bottom))
                }
                text = vaksin
                setTextColor(ResourcesCompat.getColorStateList(resources, R.color.radio_text_selector,null))
                setBackgroundResource(R.drawable.radio_vaksin_selector)
                buttonDrawable = null
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_vaksinasi, 0)
                compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.radio_button_padding_horizontal)
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.radio_button_padding_horizontal),
                    0,
                    resources.getDimensionPixelSize(R.dimen.radio_button_padding_horizontal),
                    0
                )
                typeface = ResourcesCompat.getFont(context, R.font.plus_jakarta_sans_medium)
                layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
            radioGroup.addView(radioButton)
        }
    }
}