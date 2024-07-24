package com.example.adoptify_core.ui.foster.add

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
import android.view.View
import android.view.ViewGroup
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
import androidx.fragment.app.Fragment
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentAddPetBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.foster.FosterActivity
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.adoptify_core.ui.foster.submission.SubmissionFosterActivity
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates


@RequiresApi(Build.VERSION_CODES.Q)
class AddPetFragment : Fragment() {

    private var _addPetFragment: FragmentAddPetBinding? = null

    private val addPetFragment get() = _addPetFragment!!

    private val mainViewModel: MainViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var currentUriImage: Uri? = null

    private var progressDialog: Dialog? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
        try {
            addPetFragment.previewImage.setImageURI(currentUriImage)
        } catch (e: Exception) {
            Log.d("AddVirtualPetActivity", "error: ${e.message.toString()}")
        }
    }

    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _addPetFragment = FragmentAddPetBinding.inflate(inflater, container, false)
        return _addPetFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        validateForm()
        initData()
        getToken()
        getUserId()
        addPetResult()
        setupListener()
    }

    private fun initData() {
        mainViewModel.getUserId()
        loginViewModel.getRoleId()
        loginViewModel.getSession()

    }


    private fun setupView() {
        addPetFragment.apply {
            nameEditText.addTextChangedListener(textWatcher)
            ageEditText.addTextChangedListener(textWatcher)
            descEditText.addTextChangedListener(textWatcher)
        }
    }

    //text watcher for edit text
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateForm()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun validateForm() {
        addPetFragment.apply {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString()
            val desc = descEditText.text.toString()

            val isFormValid = name.isNotEmpty() && age.isNotEmpty() && desc.isNotEmpty()
            btnSave.isEnabled = isFormValid
            btnSave.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isFormValid) R.color.primary_color_foster else R.color.btn_disabled
                )
            )
        }
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    token = it.data
                }

                is Resource.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun getUserId() {
        mainViewModel.userId.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    userId = it.data
                }

                is Resource.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun addPetHandler() {
        addPetFragment.apply {
            val categoryPet =
                radioCategory.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = addPetFragment.root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: ""

            val rasPet = radioRas.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                val radioButton = addPetFragment.root.findViewById<RadioButton>(radioButtonId)
                radioButton.text.toString()
            } ?: ""

            val gender =
                radioGender.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = addPetFragment.root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: ""

            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString()
            val desc = descEditText.text.toString()
            val image = currentUriImage?.let { uriToFile(it, requireContext()).reduceImageFile() }?.path

            val item = PetAdoptItem(
                fotoPet = image,
                umur = age.toInt(),
                gender = gender,
                namePet = name,
                ras = rasPet,
                kategori = categoryPet,
                descPet = desc,
                userId = userId,
            )

            fosterViewModel.insertAdopt(token, item)
        }
    }

    private fun addPetResult() {
        fosterViewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    popUpDialog("Yeiy!", "Penambahan data berhasil", "Selamat! Data hewan peliharaan Anda telah berhasil ditambahkan. Anda kini dapat melihat dan mengelola informasi hewan peliharaan" ,R.drawable.alert_success)
                    clearEditText()
                    (activity as? FosterActivity)?.switchTab(0)
                    Log.d("AddPetFragment", "result: ${it.data}")
                }

                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Penambahan data gagal", it.message ,R.drawable.alert_failed)
                    Log.d("AddPetFragment", "error: ${it.message}")
                }
            }
        }
    }

    private fun setupListener() {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        addPetFragment.apply {
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

            btnSave.setOnClickListener { addPetHandler() }

            headerFoster.btnPengajuan.setOnClickListener { startActivity(Intent(requireContext(), SubmissionFosterActivity::class.java), options.toBundle()) }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBarDialog() else dismissProgressDialog()
    }

    private fun popUpDialog(title: String, desc: String, subDesc: String, image: Int) {
        val dialog = Dialog(requireContext())
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

            imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener { dismiss() }
            show()
        }
    }

    private fun clearEditText() {
        addPetFragment.apply {
            nameEditText.text?.clear()
            ageEditText.text?.clear()
            descEditText.text?.clear()
            radioCategory.clearCheck()
            radioRas.clearCheck()
            radioGender.clearCheck()
            currentUriImage = null
            previewImage.setImageResource(R.drawable.ic_preview_image)
        }
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(requireContext()).apply {
                val view =
                    LayoutInflater.from(requireContext()).inflate(R.layout.dialog_progress, null)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.indeterminateTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_color_foster)
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

    override fun onDestroyView() {
        dismissProgressDialog()
        super.onDestroyView()
        _addPetFragment = null
    }

    override fun onPause() {
        dismissProgressDialog()
        super.onPause()
    }

}