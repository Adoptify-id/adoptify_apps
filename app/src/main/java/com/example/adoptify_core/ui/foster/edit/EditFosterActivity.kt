package com.example.adoptify_core.ui.foster.edit

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
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
import com.example.adoptify_core.databinding.ActivityEditFosterBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.foster.FosterActivity
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.domain.model.DataAdopt
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class EditFosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditFosterBinding

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var token = ""
    private var petId by Delegates.notNull<Int>()
    private var userId by Delegates.notNull<Int>()

    private var currentUriImage: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
        try {
            binding.previewImage.setImageURI(currentUriImage)
        } catch (e: Exception) {
            Log.d("EditPetActivity", "error: ${e.message.toString()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        setupView()
        initData()
        getDetail()
        updateResult()
    }

    private fun setupView() {
        binding.apply {
            binding.headerFoster.txtHeader.text = "Edit Pet"
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
                            val imageUrl =
                                "https://storage.googleapis.com/bucket-adoptify/imagesUser/${it?.fotoPet}"
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
            val image = currentUriImage?.let { uriToFile(it, this@EditFosterActivity).reduceImageFile() }

            val data = PetAdoptItem(
                fotoPet = image.toString(),
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

    private fun updateResult() {
        fosterViewModel.detail.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    popUpDialog("Yeiy!", "Proses edit pet berhasil", R.drawable.alert_success)
                    Log.d("UpdatePet", "updateResult: ${it.data}")
                }
                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Proses edit pet gagal", R.drawable.alert_failed)
                    Log.d("UpdatePet", "error: ${it.message}")
                }
            }
        }
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

            imageView.setImageDrawable(ContextCompat.getDrawable(this@EditFosterActivity, image))
            titleText.text = title
            descText.text = desc
            btnClose.setOnClickListener {
                dismiss()
                startActivity(Intent(this@EditFosterActivity, FosterActivity::class.java))
                finish()
            }
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}