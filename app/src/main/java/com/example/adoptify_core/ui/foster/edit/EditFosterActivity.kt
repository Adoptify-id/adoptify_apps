package com.example.adoptify_core.ui.foster.edit

import android.app.Dialog
import android.content.Intent
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
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityEditFosterBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.foster.FosterActivity
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.adoptify_core.ui.foster.submission.SubmissionFosterActivity
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.utils.convertUrlToFile
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class EditFosterActivity : BaseActivity() {

    private lateinit var binding: ActivityEditFosterBinding

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var token = ""
    private var petId by Delegates.notNull<Int>()
    private var userId by Delegates.notNull<Int>()

    private var currentUriImage: Uri? = null
    private var lastUpdatedData = PetAdoptItem()
    private var progressDialog: Dialog? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            currentUriImage = it
            try {
                binding.previewImage.setImageURI(currentUriImage)
                validateForm()
            } catch (e: Exception) {
                Log.d("EditPetActivity", "error: ${e.message.toString()}")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setupListener()
        validateForm()
        setupView()
        initData()
        getDetail()
        updateResult()
    }

    private fun setupView() {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.apply {
            headerFoster.apply {
                txtHeader.text = "Edit Pet"
                btnPengajuan.setOnClickListener {
                    startActivity(Intent(this@EditFosterActivity, SubmissionFosterActivity::class.java), options.toBundle())
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_to_submission_foster")
                    firebaseAnalytics.logEvent("navigate_to_submission_foster", bundle)
                }
                btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            }
            nameEditText.addTextChangedListener(textWatcher)
            descEditText.addTextChangedListener(textWatcher)
            ageEditText.addTextChangedListener(textWatcher)
            radioCategory.setOnCheckedChangeListener { _, _ -> validateForm() }
            radioRas.setOnCheckedChangeListener { _, _ -> validateForm() }
            radioGender.setOnCheckedChangeListener { _, _ -> validateForm() }
        }
    }

    private fun initData() {
        token = intent?.getStringExtra("TOKEN") ?: ""
        petId = intent.getIntExtra("PET_ID", 0)
        userId = intent.getIntExtra("USER_ID", 0)

        adoptViewModel.getDetailPet(token, petId)
    }

    private fun setupListener() {
        binding.apply {
            btnAdd.setOnClickListener { galleryLauncher.launch("image/*") }

            radioCatEdit.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    updateRadioButtonsForCategory("Kucing")
                }
            }

            radioDogEdit.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    updateRadioButtonsForCategory("Anjing")
                }
            }

            btnSave.setOnClickListener { updateHandler() }
        }
    }

    private fun updateRadioButtonsForCategory(category: String) {
        binding.apply {
            if (category == "Kucing") {
                radioRasPet1.text = getString(R.string.anggora)
                radioRasPet2.text = getString(R.string.domestik)
                radioRasPet3.text = getString(R.string.ragdoll)
                radioRasPet4.text = getString(R.string.persia)
                radioRasPet5.text = getString(R.string.maine_coon)
            } else if (category == "Anjing") {
                radioRasPet1.text = getString(R.string.beagie)
                radioRasPet2.text = getString(R.string.bulldog)
                radioRasPet3.text = getString(R.string.poodie)
                radioRasPet4.text = getString(R.string.maltese)
                radioRasPet5.text = getString(R.string.bitchon_frise)
            }
        }
    }

    private fun getDetail() {
        adoptViewModel.detail.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    it.data.data?.map {
                        binding.apply {
                            val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesPet/${it?.fotoPet}"
                            nameEditText.setText(it?.namePet)
                            ageEditText.setText(it?.umur.toString())
                            descEditText.setText(it?.descPet)
                            Glide.with(this@EditFosterActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_preview_image)
                                .into(previewImage)

                            when (it?.kategori) {
                                "Kucing" -> radioCategory.check(R.id.radio_cat_edit)
                                "Anjing" -> radioCategory.check(R.id.radio_dog_edit)
                            }

                            when (it?.gender) {
                                "Betina" -> radioGender.check(R.id.radio_female_edit)
                                "Jantan" -> radioGender.check(R.id.radio_male_edit)
                            }

                            when (it?.ras) {
                                getString(R.string.anggora) -> radioRasPet1.isChecked = true
                                getString(R.string.domestik) -> radioRasPet2.isChecked = true
                                getString(R.string.ragdoll) -> radioRasPet3.isChecked = true
                                getString(R.string.persia) -> radioRasPet4.isChecked = true
                                getString(R.string.maine_coon) -> radioRasPet5.isChecked = true
                                getString(R.string.beagie) -> radioRasPet1.isChecked = true
                                getString(R.string.bulldog) -> radioRasPet2.isChecked = true
                                getString(R.string.poodie) -> radioRasPet3.isChecked = true
                                getString(R.string.maltese) -> radioRasPet4.isChecked = true
                                getString(R.string.bitchon_frise) -> radioRasPet5.isChecked = true
                            }

                            lastUpdatedData = PetAdoptItem(
                                namePet = it?.namePet,
                                umur = it?.umur,
                                descPet = it?.descPet,
                                fotoPet = it?.fotoPet,
                                kategori = it?.kategori,
                                gender = it?.gender,
                                ras = it?.ras
                            )

                        }
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("EditPet", "error: ${it.message}")
                }
            }
        }
    }

    private fun updateHandler() {
        binding.apply {
            val categoryPet =
                radioCategory.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: ""

            val rasPet = radioRas.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                val radioButton = root.findViewById<RadioButton>(radioButtonId)
                radioButton.text.toString()
            } ?: ""

            val gender =
                radioGender.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: ""

            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString()
            val desc = descEditText.text.toString()
            lifecycleScope.launch {
                val imageFile = if (currentUriImage != null) {
                    uriToFile(currentUriImage!!, this@EditFosterActivity).reduceImageFile().path
                } else {
                    lastUpdatedData.fotoPet?.let {
                        val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesPet/$it"
                        convertUrlToFile(this@EditFosterActivity,imageUrl)?.path ?: it
                    }
                }
                val finalImageFile = imageFile ?: ""

                val data = PetAdoptItem(
                    fotoPet = finalImageFile,
                    umur = age.toInt(),
                    gender = gender,
                    ras = rasPet,
                    descPet = desc,
                    namePet = name,
                    kategori = categoryPet,
                    userId = userId
                )

                fosterViewModel.updatePetAdopt(token, petId, data)
            }
        }
    }

    private fun validateForm() {
        binding.apply {
            val isRadioCategorySelected = radioCategory.checkedRadioButtonId != -1
            val isRadioRasSelected = radioRas.checkedRadioButtonId != -1
            val isRadioGenderSelected = radioGender.checkedRadioButtonId != -1
            val namePet = nameEditText.text.toString()
            val agePet = ageEditText.text.toString()
            val descPet = descEditText.text.toString()

            val selectedCategory = if (isRadioCategorySelected) findViewById<RadioButton>(radioCategory.checkedRadioButtonId).text.toString() else null
            val selectedRas = if (isRadioRasSelected) findViewById<RadioButton>(radioRas.checkedRadioButtonId).text.toString() else null
            val selectedGender = if (isRadioGenderSelected) findViewById<RadioButton>(radioGender.checkedRadioButtonId).text.toString() else null

            val isNameChanged = namePet != lastUpdatedData.namePet && namePet.isNotEmpty()
            val isAgeChanged = agePet != lastUpdatedData.umur.toString() && agePet.isNotEmpty()
            val isDescChanged = descPet != lastUpdatedData.descPet && descPet.isNotEmpty()
            val isImageChanged = currentUriImage != null
            val isCategoryChanged = selectedCategory != lastUpdatedData.kategori && isRadioCategorySelected
            val isRasChanged = selectedRas != lastUpdatedData.ras && isRadioRasSelected
            val isGenderChanged = selectedGender != lastUpdatedData.gender && isRadioGenderSelected

            val isFormValid = isNameChanged || isAgeChanged || isDescChanged || isImageChanged || isCategoryChanged || isRasChanged || isGenderChanged
            btnSave.isEnabled = isFormValid
            btnSave.backgroundTintList = ContextCompat.getColorStateList(this@EditFosterActivity, if (isFormValid) R.color.primary_color_foster else R.color.btn_disabled)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { validateForm() }
    }

    private fun updateResult() {
        fosterViewModel.detail.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    popUpDialog(
                        "Yeiy!",
                        "Pengeditan data berhasil",
                        "Selamat! Data hewan Anda telah berhasil diperbarui. Perubahan yang Anda lakukan telah disimpan dengan sukses.",
                        R.drawable.alert_success
                    )
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_edit_pet_adopt")
                    firebaseAnalytics.logEvent("edit_pet_adopt", bundle)
                    Log.d("UpdatePet", "updateResult: ${it.data}")
                }

                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog(
                        "Yah!",
                        "Pengeditan data gagal",
                        it.message,
                        R.drawable.alert_failed
                    )
                    Log.d("UpdatePet", "error: ${it.message}")
                }
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
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val imageView = dialog.findViewById<ImageView>(R.id.img_alert)
            val titleText = dialog.findViewById<TextView>(R.id.title_alert)
            val descText = dialog.findViewById<TextView>(R.id.desc_alert)
            val subDescText = dialog.findViewById<TextView>(R.id.sub_desc_alert)
            val btnClose = dialog.findViewById<Button>(R.id.btnClose)
            titleText.setTextColor(ContextCompat.getColor(this@EditFosterActivity,R.color.primary_color_foster))
            btnClose.backgroundTintList = ContextCompat.getColorStateList(this@EditFosterActivity, R.color.primary_color_foster)
            imageView.setImageDrawable(ContextCompat.getDrawable(this@EditFosterActivity, image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener {
                dismiss()
                startActivity(Intent(this@EditFosterActivity, FosterActivity::class.java))
                finish()
            }
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBarDialog() else dismissProgressDialog()
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(this).apply {
                val view =
                    LayoutInflater.from(this@EditFosterActivity).inflate(R.layout.dialog_progress, null)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.indeterminateTintList = ContextCompat.getColorStateList(this@EditFosterActivity, R.color.primary_color_foster)
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

    override fun onPause() {
        dismissProgressDialog()
        super.onPause()
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }
}