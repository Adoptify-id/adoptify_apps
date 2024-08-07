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
import com.google.firebase.analytics.FirebaseAnalytics
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
            validateForm()
        } catch (e: Exception) {
            Log.d("AddVirtualPetActivity", "error: ${e.message.toString()}")
        }
    }
    private val virtualPetViewModel: VirtualPetViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()
    private var token: String? = null
    private var userId: Int? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVirtualPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
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
        binding.apply {
            header.txtBookmark.text = resources.getString(R.string.virtual_pet)
            nameEditText.addTextChangedListener(textWatcher)
            ageEditText.addTextChangedListener(textWatcher)
            weightEditText.addTextChangedListener(textWatcher)
            radioCategory.setOnCheckedChangeListener { _, _ -> validateForm() }
            radioRas.setOnCheckedChangeListener { _, _ -> validateForm() }
            radioGender.setOnCheckedChangeListener { _, _ -> validateForm() }
        }
    }

    private fun validateForm() {
        binding.apply {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
            val weight = weightEditText.text.toString().takeIf { it.isNotEmpty() }?.toFloatOrNull() ?: 0f
            val image = currentUriImage?.let { uriToFile(it, this@AddVirtualPetActivity).reduceImageFile() }?.path
            val isRadioGroupCategorySelected = radioCategory.checkedRadioButtonId != -1
            val isRadioRasGroupCategorySelected = radioRas.checkedRadioButtonId != -1
            val isRadioGenderGroupCategorySelected = radioGender.checkedRadioButtonId != -1

            val isImageValid = image != null && image.isNotEmpty()
            val isFormValid = name.isNotEmpty() && age > 0 && weight > 0 && isImageValid && isRadioGroupCategorySelected && isRadioRasGroupCategorySelected && isRadioGenderGroupCategorySelected
            btnSave.isEnabled = isFormValid
            btnSave.backgroundTintList = ContextCompat.getColorStateList(this@AddVirtualPetActivity, if (isFormValid) R.color.primaryColor else R.color.btn_disabled)
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
            val beratPet = weightEditText.text.toString().takeIf { it.isNotEmpty() }?.toFloatOrNull() ?: 0f
            val imageFile = currentUriImage?.let { uriToFile(it, this@AddVirtualPetActivity).reduceImageFile() }?.path

            val item = AddVirtualPetItem(
                name = namePet,
                umur = agePet.toInt(),
                gender = gender,
                ras = rasPet,
                kategori = categoryPet,
                fotoPet = imageFile,
                beratPet = beratPet,
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
                    popUpDialog(
                        title = "Yeiy!",
                        desc = "tambah data virtual pet sukses",
                        subDesc = "Anda telah berhasil menambahkan data virtual pet. Data dapat diakses kapan saja. Terima kasih!",
                        image = R.drawable.alert_success
                    ) {
                        setResult(Activity.RESULT_OK)
                        finish()

                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_submit_add_virtual_pet")
                        firebaseAnalytics.logEvent("submit_add_virtual_pet", bundle)
                    }
                    Log.d("AddVirtualPetActivity", "result: ${it.data}")
                }

                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "tambah data virtual pet gagal", it.message, R.drawable.alert_failed)
                    Log.d("AddVirtualPetActivity", "error: ${it.message}")
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { validateForm() }
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

    private fun popUpDialog(title: String, desc: String, subDesc: String, image: Int, onDismiss: () -> Unit = {}) {
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

            imageView.setImageDrawable(ContextCompat.getDrawable(this@AddVirtualPetActivity, image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener {
                dismiss()
                onDismiss()
            }
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

