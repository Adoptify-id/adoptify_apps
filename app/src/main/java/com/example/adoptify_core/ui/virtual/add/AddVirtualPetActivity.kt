package com.example.adoptify_core.ui.virtual.add

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.adoptify_core.databinding.ActivityAddVirtualPetBinding
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.utils.SessionViewModel
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel

@RequiresApi(Build.VERSION_CODES.Q)
class AddVirtualPetActivity : BaseActivity() {

    private lateinit var binding: ActivityAddVirtualPetBinding

    private var currentUriImage: Uri? = null

    private var progressDialog: Dialog? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
        try {
            binding.previewImage.setImageURI(currentUriImage)
        } catch (e: Exception) {
            Log.d("AddVirtualPetActivity", "error: ${e.message.toString()}")
        }
    }
    private val virtualPetViewModel: VirtualPetViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()
    private var token: String? = null
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVirtualPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        setupView()
        setupListener()
        validateForm()
        addVirtualPetResult()
    }

    private fun observeData() {
        sessionViewModel.token.observe(this) { token = it }
        sessionViewModel.userId.observe(this) { userId = it }
    }

    private fun setupListener() {
        binding.apply {
            btnAdd.setOnClickListener { galleryLauncher.launch("image/*") }

            radioCat.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    radioRasPet1.text = getString(R.string.anggora)
                    radioRasPet2.text = getString(R.string.domestik)
                    radioRasPet3.text = getString(R.string.ragdoll)
                    radioRasPet4.text = getString(R.string.persia)
                    radioRasPet5.text = getString(R.string.maine_coon)
                }
            }

            radioDog.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    radioRasPet1.text = getString(R.string.beagie)
                    radioRasPet2.text = getString(R.string.bulldog)
                    radioRasPet3.text = getString(R.string.poodie)
                    radioRasPet4.text = getString(R.string.maltese)
                    radioRasPet5.text = getString(R.string.bitchon_frise)
                }
            }

            btnSave.setOnClickListener { virtualPetHandler() }
            header.btnBack.setOnClickListener {  onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun setupView() {
        binding.header.txtBookmark.text = resources.getString(R.string.virtual_pet)
        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.ageEditText.addTextChangedListener(textWatcher)
    }

    private fun validateForm() {
        binding.apply {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString()

            val isFormValid = name.isNotEmpty() && age.isNotEmpty()
            btnSave.isEnabled = isFormValid
            btnSave.setBackgroundColor(
                ContextCompat.getColor(
                    this@AddVirtualPetActivity,
                    if (isFormValid) R.color.primaryColor else R.color.btn_disabled
                )
            )
        }
    }

    private fun virtualPetHandler() {
        binding.apply {

            val categoryPet =
                radioCategory.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = binding.root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: ""

            val rasPet = radioRas.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                val radioButton = binding.root.findViewById<RadioButton>(radioButtonId)
                radioButton.text.toString()
            } ?: ""

            val gender =
                radioGender.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = binding.root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: ""

            val namePet = nameEditText.text.toString()
            val agePet = ageEditText.text.toString()
            val beratPet = weightEditText.text.toString()
            val imageFile =
                currentUriImage?.let { uriToFile(it, this@AddVirtualPetActivity).reduceImageFile() }?.path

            val item = AddVirtualPetItem(
                name = namePet,
                umur = agePet.toInt(),
                gender = gender,
                ras = rasPet,
                kategori = categoryPet,
                fotoPet = imageFile,
                beratPet = beratPet.toInt(),
                user_id = userId
            )

            virtualPetViewModel.insertVirtualPet(token!!, item)
        }
    }

    private fun addVirtualPetResult() {
        virtualPetViewModel.result.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("AddVirtualPetActivity", "result: ${it.data}")
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Penambahan data virtual pet gagal", it.message, R.drawable.alert_failed)
                    Log.d("AddVirtualPetActivity", "error: ${it.message}")
                }

                else -> {}
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validateForm()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBarDialog() else dismissProgressDialog()
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(this).apply {
                val view = LayoutInflater.from(this@AddVirtualPetActivity).inflate(R.layout.dialog_progress, null)
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

            imageView.setImageDrawable(ContextCompat.getDrawable(this@AddVirtualPetActivity, image))
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

