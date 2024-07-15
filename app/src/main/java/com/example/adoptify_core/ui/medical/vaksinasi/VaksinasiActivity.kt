package com.example.adoptify_core.ui.medical.vaksinasi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityVaksinasiBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.medical.MedicalRecordViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.VaksinasiItem
import com.example.core.utils.ForceLogout
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class VaksinasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVaksinasiBinding
    private val medicalViewModel: MedicalRecordViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()
    private var progressDialog: Dialog? = null
    var valueDate = ""
    var titleDialog = ""
    var petVaksin = mutableListOf("Distemper", "Parvovirus", "Hepatitis", "Rabies", "Leptospira", "Parainfluenza", "Bordetella")
    var vaksinSelected = ""
    private var token: String? = null
    private var userId: Int? = null
    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaksinasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.txtBookmark.text = resources.getString(R.string.vaksinasi)
        observeData()
        forceLogout()
        setupView()
        validateForm()
        setupListener()
        vaksinasiResult()
    }

    private fun observeData() {
        sessionViewModel.token.observe(this) {
            token = it
        }

        sessionViewModel.userId.observe(this) {
            userId = it
        }
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
            btnLogin.backgroundTintList = ContextCompat.getColorStateList(this@VaksinasiActivity, R.color.primary_color_foster)

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

    private fun setupView() {
        binding.apply {
            namePetEditText.addTextChangedListener(textWatcher)
            conditionPetEditText.addTextChangedListener(textWatcher)
            descPetEditText.addTextChangedListener(textWatcher)
            weightPetEditText.addTextChangedListener(textWatcher)
            addInfoEditText.addTextChangedListener(textWatcher)
            nameClinicEditText.addTextChangedListener(textWatcher)
            nameDoctorEditText.addTextChangedListener(textWatcher)
            addressClinicEditText.addTextChangedListener(textWatcher)
            medicalEditText.addTextChangedListener(textWatcher)

            radioCategory.setOnCheckedChangeListener { _, _ ->  validateForm()}
        }
    }

    private fun setupListener() {
        binding.apply {
            header.btnBack.setOnClickListener {  onBackPressedDispatcher.onBackPressed() }
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
        binding.apply {
            val categoryPet = radioCategory.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                val radioButton = binding.root.findViewById<RadioButton>(radioButtonId)
                radioButton.text.toString()
            } ?: ""

            val name = namePetEditText.text.toString()
            val kesehatan = conditionPetEditText.text.toString()
            val descKesehatan = descPetEditText.text.toString()
            val beratPet = weightPetEditText.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
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
                beratPet = beratPet,
                info = info,
                klinikName = klinikName,
                dokterName = doctorName,
                alamat = alamat,
                tanggal = tanggal,
                jenisVaksin = jenisVaksin,
                catatan = catatan,
                userId = userId!!
            )
            medicalViewModel.insertVaksinasi(token!!, data)
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
            val klinikName = nameClinicEditText.text.toString()
            val doctorName = nameDoctorEditText.text.toString()
            val alamat = addressClinicEditText.text.toString()
            val tanggal = valueDate
            val jenisVaksin = vaksinSelected
            val catatan = medicalEditText.text.toString()

            val isFormValid = name.isNotEmpty() && kesehatan.isNotEmpty() && descKesehatan.isNotEmpty() && beratPet > 0 && info.isNotEmpty() && klinikName.isNotEmpty() && doctorName.isNotEmpty() && alamat.isNotEmpty() && tanggal.isNotEmpty() && jenisVaksin.isNotEmpty() && catatan.isNotEmpty() && isRadioGroupCategorySelected
            btnSave.isEnabled = isFormValid
            btnSave.backgroundTintList = ContextCompat.getColorStateList(
                this@VaksinasiActivity,
                if (isFormValid) R.color.pink else R.color.btn_disabled
            )
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {validateForm()}
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
                    popUpDialog("Yah!", "Penambahan data vaksin gagal", it.message, R.drawable.alert_failed)
                    Log.d("InsertVaksinasi", "error: ${it.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
//        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

            if (vaksinSelected.isNotEmpty()) {
                val selectedRadioButton = radio_group.findViewWithTag<RadioButton>(vaksinSelected)
                selectedRadioButton?.isChecked = true
            }

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
                tag = vaksin
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

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(this).apply {
                val view = LayoutInflater.from(this@VaksinasiActivity).inflate(R.layout.dialog_progress, null)
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

            imageView.setImageDrawable(ContextCompat.getDrawable(this@VaksinasiActivity, image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener { dismiss() }
            show()
        }
    }

    override fun onDestroy() {
        dismissProgressDialog()
        logoutDialog?.dismiss()
        super.onDestroy()
    }

    override fun onPause() {
        dismissProgressDialog()
        logoutDialog?.dismiss()
        super.onPause()
    }
}