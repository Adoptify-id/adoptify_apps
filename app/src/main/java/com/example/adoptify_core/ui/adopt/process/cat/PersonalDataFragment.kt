package com.example.adoptify_core.ui.adopt.process.cat

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentPersonalDataBinding
import com.example.adoptify_core.ui.adopt.PersonalData
import com.example.adoptify_core.ui.adopt.SharedDataViewModel
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import com.example.core.utils.SessionViewModel
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel

@RequiresApi(Build.VERSION_CODES.Q)
class PersonalDataFragment : Fragment() {

    private var _personalData: FragmentPersonalDataBinding? = null

    private val personalData get() = _personalData!!

    private val profileViewModel: ProfileViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()
    private lateinit var sharedViewModel: SharedDataViewModel

    private var token: String? = null
    private var userId: Int? = null

    private var currentUriImage: Uri? = null
        set(value) {
            field = value
            showIndicatorSuccess(value != null && value.toString().isNotEmpty())
            validateForm()
        }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _personalData = FragmentPersonalDataBinding.inflate(inflater, container, false)
        return _personalData?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]


        observeData()
        setupView()
        validateForm()
        setupListener()
    }

    private fun setupView() {
        personalData.apply {
            statusAdopt.line.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryColor
                )
            )
            statusAdopt.icPersonalData.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_personal_data_enable
                )
            )

            usernameEditText.addTextChangedListener(textWatcher)
            ageEditText.addTextChangedListener(textWatcher)
            addressEditText.addTextChangedListener(textWatcher)
            telephoneEditText.addTextChangedListener(textWatcher)
            jobEditText.addTextChangedListener(textWatcher)
            rangeEditText.addTextChangedListener(textWatcher)
            medsosEditText.addTextChangedListener(textWatcher)
        }
    }

    private fun observeData() {
        sessionViewModel.token.observe(viewLifecycleOwner) {
            token = it
            if (token != null && userId != null) {
                profileViewModel.getDetailUser(token!!, userId!!)
            }
        }

        sessionViewModel.userId.observe(viewLifecycleOwner) {
            userId = it
            if (token != null && userId != null) {
                profileViewModel.getDetailUser(token!!, userId!!)
            }
        }
    }


    private fun getProfileUser() {
        profileViewModel.detail.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    if (personalData.switchButton.isChecked) {
                        it.data.data?.map { profile ->
                            personalData.usernameEditText.setText(profile?.fullName)
                            personalData.telephoneEditText.setText(profile?.noTelp)
                            personalData.addressEditText.setText(profile?.alamat)
                            Log.d(
                                "PersonalDataFragment",
                                "username: ${profile?.username} ${profile?.noTelp}  ${profile?.alamat}"
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("PersonalDataFragment", "error: ${it.message}")
                }
            }
        }
    }


    private fun setupListener() {
        personalData.apply {
            switchButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getProfileUser()
                } else {
                    clearEditText()
                }
            }

            val viewPager = activity?.findViewById<ViewPager2>(R.id.processViewPager)
            btnNext.setOnClickListener {
                val imageFile = currentUriImage?.let { uriToFile(it, requireContext()).reduceImageFile() }?.path
                val data = PersonalData(
                    name = usernameEditText.text.toString(),
                    age = ageEditText.text.toString().toInt(),
                    telephone = telephoneEditText.text.toString(),
                    address = addressEditText.text.toString(),
                    job = jobEditText.text.toString(),
                    range = rangeEditText.text.toString(),
                    medsos = medsosEditText.text.toString(),
                    image = imageFile
                )
                sharedViewModel.personalData.value = data
                viewPager?.currentItem = 2
            }
            btnBack.setOnClickListener { viewPager?.currentItem = 0 }

            cardUpload.card.setOnClickListener { galleryLauncher.launch("image/*") }
        }
    }

    private fun clearEditText() {
        personalData.usernameEditText.text?.clear()
        personalData.telephoneEditText.text?.clear()
        personalData.addressEditText.text?.clear()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) { validateForm() }
    }

    private fun validateForm() {
        personalData.apply {
            val name = usernameEditText.text.toString()
            val age = ageEditText.text.toString()
            val telp = telephoneEditText.text.toString()
            val address = addressEditText.text.toString()
            val job = jobEditText.text.toString()
            val range = rangeEditText.text.toString()
            val medsos = medsosEditText.text.toString()

            val isFormValid = currentUriImage != null &&
                name.isNotEmpty() && age.isNotEmpty() && telp.isNotEmpty() && address.isNotEmpty() && job.isNotEmpty() && range.isNotEmpty() && medsos.isNotEmpty()
            btnNext.isEnabled = isFormValid
            btnNext.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (isFormValid) R.color.primaryColor else R.color.btn_disabled
            )
            btnNext.setTextColor(
                if (isFormValid) resources.getColor(R.color.white) else resources.getColor(
                    R.color.black
                )
            )
        }
    }


    private fun showLoading(isLoading: Boolean) {
        personalData.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showIndicatorSuccess(isSuccess: Boolean) {
        personalData.cardUpload.successUpload.visibility =
            if (isSuccess) View.VISIBLE else View.GONE
        validateForm()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _personalData = null
    }
}