package com.example.adoptify_core.ui.virtual.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityAddVirtualPetBinding
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.adoptify_core.ui.virtual.main.VirtualPetActivity
import com.example.core.data.Resource
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class AddVirtualPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddVirtualPetBinding

    private var currentUriImage: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
        try {
            binding.previewImage.setImageURI(currentUriImage)
        } catch (e: Exception) {
            Log.d("AddVirtualPetActivity", "error: ${e.message.toString()}")
        }
    }

    private val virtualPetViewModel: VirtualPetViewModel by viewModel()
    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVirtualPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupListener()
        validateForm()
        addVirtualPetResult()
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
            header.btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun setupView() {
        binding.header.txtBookmark.text = resources.getString(R.string.virtual_pet)

        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.ageEditText.addTextChangedListener(textWatcher)

        token = intent?.getStringExtra("token") ?: ""
        userId = intent.getIntExtra("userId", 0)
        Log.d("AddVirtualPetActivity", "userId: $userId")
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
                currentUriImage?.let { uriToFile(it, this@AddVirtualPetActivity).reduceImageFile() }

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

            virtualPetViewModel.insertVirtualPet(token, item)
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
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}