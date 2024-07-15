package com.example.adoptify_core.ui.adopt.process.cat

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentThirdSubmissionBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.adopt.SharedDataViewModel
import com.example.adoptify_core.ui.adopt.submission.SubmissionAdoptActivity
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.FormItem
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class ThirdSubmissionFragment : Fragment() {

    private var _thirdFragment: FragmentThirdSubmissionBinding? = null
    private val thirdFragment get() = _thirdFragment!!

    private val adoptViewModel: AdoptViewModel by viewModel()

    private val sessionViewModel: SessionViewModel by viewModel()

    private var progressDialog: Dialog? = null

    private lateinit var sharedViewModel: SharedDataViewModel

    private var token: String? = null
    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _thirdFragment = FragmentThirdSubmissionBinding.inflate(inflater, container, false)
        return _thirdFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]

        setupView()
        validateForm()
        observeData()
        formResult()
        setupListener()
    }

    private fun observeData() {
        sessionViewModel.token.observe(viewLifecycleOwner) {
            token = it
            Log.d("ProcessAdopt", "token: $token")
        }
        sessionViewModel.userId.observe(viewLifecycleOwner) {
            userId = it
            Log.d("ProcessAdopt", "token: $userId")
        }
    }

    private fun setupView() {
        thirdFragment.apply {
            statusAdopt.line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            statusAdopt.icPersonalData.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_personal_data_enable))
            statusAdopt.line2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            statusAdopt.icSubmission.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_submission_enable))

            adoptPetEditText.addTextChangedListener(textWatcher)
            photoEditText.addTextChangedListener(textWatcher)
            otherEditText.addTextChangedListener(textWatcher)
            otherLiveEditText.addTextChangedListener(textWatcher)

            radioGroupTempatTinggal.setOnCheckedChangeListener{_, _ -> validateForm()}
            radioGroupAdopt.setOnCheckedChangeListener{_, _ -> validateForm()}
            radioGroupLive.setOnCheckedChangeListener{_, _ -> validateForm()}
        }
    }

    private fun setupListener() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.processViewPager)

        thirdFragment.btnNext.setOnClickListener {formHandler() }
        thirdFragment.btnBack.setOnClickListener { viewPager?.currentItem = 3 }
    }

    private fun formHandler() {
        thirdFragment.apply {
            val residencePet =
                radioGroupTempatTinggal.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = thirdFragment.root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: otherEditText.text.toString()

            val adoptPet = radioGroupAdopt.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                val radioButton = thirdFragment.root.findViewById<RadioButton>(radioButtonId)
                radioButton.text.toString()
            } ?: ""

            val livePet =
                radioGroupLive.checkedRadioButtonId.takeIf { it != -1 }?.let { radioButtonId ->
                    val radioButton = thirdFragment.root.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                } ?: otherLiveEditText.text.toString()

            val adopt = adoptPetEditText.text.toString()
            val photo = photoEditText.text.toString()

            val personalData = sharedViewModel.personalData.value
            val submissionData = sharedViewModel.submissionData.value
            val secondSubmissionData = sharedViewModel.secondSubmissionData.value
            val petId = sharedViewModel.petId.value

            val item = FormItem(
                name = personalData?.name,
                umur = personalData?.age,
                noWa = personalData?.telephone,
                domisili = personalData?.address,
                pekerjaan = personalData?.job,
                rangePendapatan = personalData?.range,
                medsos = personalData?.medsos,
                kartuIdentitas = personalData?.image,
                umum1 = submissionData?.reason,
                umum2 = submissionData?.hope,
                umum3 = submissionData?.maintain,
                umum4 = submissionData?.beside,
                umum5 = submissionData?.vaccine,
                umum6 = submissionData?.adopter,
                ppk1 = secondSubmissionData?.food,
                ppk2 = secondSubmissionData?.merk,
                ppk3 = secondSubmissionData?.drink,
                ppk4 = secondSubmissionData?.radioCage,
                ppk5 = secondSubmissionData?.vaccine,
                ppk6 = secondSubmissionData?.medicine,
                ppk7 = secondSubmissionData?.radioPrivacy,
                ppk8 = secondSubmissionData?.move,
                ppk9 = secondSubmissionData?.radioNews,
                ppk10 = secondSubmissionData?.selectedCheckBoxes,
                rl1 = residencePet,
                rl2 = adoptPet,
                rl3 = livePet,
                rl4 = adopt,
                rl5 = photo,
                userId = userId,
                petId = petId,
            )

            adoptViewModel.formAdopt(token!!, item)
        }

    }

    private fun formResult() {
        adoptViewModel.form.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    popUpDialog("Yeiy!", "Pengajuan adopsi gagal", "Selamat! Pengajuan adopsi berhasil ditambahkan. Anda kini dapat melihat informasi pengajuan hewan" ,R.drawable.alert_success)
                    Log.d("ProcessAdopt", "result: ${it.data}")
                }
                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Pengajuan adopsi gagal", it.message ,R.drawable.alert_failed)
                    Log.d("ProcessAdopt", "error: ${it.message}")
                }
            }
        }
    }

    private fun validateForm() {
        thirdFragment.apply {
            val adopt = adoptPetEditText.text.toString()
            val photo = photoEditText.text.toString()
            val other = otherEditText.text.toString()
            val otherLive = otherLiveEditText.text.toString()

            val isRadioTempatTinggalSelected = radioGroupTempatTinggal.checkedRadioButtonId != -1
            val isRadioAdoptSelected = radioGroupAdopt.checkedRadioButtonId != -1
            val isRadioLiveSelected = radioGroupLive.checkedRadioButtonId != -1

            val isOtherTextFieldValid = !isRadioTempatTinggalSelected && other.isNotEmpty()
            val isOtherLiveTextFieldValid = !isRadioLiveSelected && otherLive.isNotEmpty()

            val isFormValid = adopt.isNotEmpty() && photo.isNotEmpty() && isRadioAdoptSelected &&
                    (isRadioTempatTinggalSelected || isOtherTextFieldValid) &&
                    (isRadioLiveSelected || isOtherLiveTextFieldValid)

            btnNext.isEnabled = isFormValid
            btnNext.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (isFormValid) R.color.primaryColor else R.color.btn_disabled
            )
            btnNext.setTextColor(if (isFormValid) resources.getColor(R.color.white) else resources.getColor(R.color.black))
        }
    }


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {validateForm()}
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
            btnClose.setOnClickListener {
                startActivity(Intent(requireContext(), SubmissionAdoptActivity::class.java))
                activity?.finish()
                dismiss()
            }
            show()
        }
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(requireContext()).apply {
                val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_progress, null)
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

    override fun onDestroyView() {
        dismissProgressDialog()
        super.onDestroyView()
        _thirdFragment = null
    }

    override fun onPause() {
        dismissProgressDialog()
        super.onPause()
    }


}