package com.example.adoptify_core.ui.adopt.process.cat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentSubmissionBinding
import com.example.adoptify_core.ui.adopt.SharedDataViewModel
import com.example.adoptify_core.ui.adopt.SubmissionData


class SubmissionFragment : Fragment() {

    private var _submissionFragment: FragmentSubmissionBinding? = null

    private val submissionFragment get() = _submissionFragment!!
    private lateinit var sharedViewModel: SharedDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _submissionFragment = FragmentSubmissionBinding.inflate(inflater, container, false)
        return _submissionFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]

        setupView()
        validateForm()
        setupListener()
    }

    private fun setupView() {
        submissionFragment.apply {
            statusAdopt.line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            statusAdopt.icPersonalData.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_personal_data_enable))
            statusAdopt.line2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            statusAdopt.icSubmission.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_submission_enable))

            reasonEditText.addTextChangedListener(textWatcher)
            hopeEditText.addTextChangedListener(textWatcher)
            maintainEditText.addTextChangedListener(textWatcher)
            besideEditText.addTextChangedListener(textWatcher)
            vaccineEditText.addTextChangedListener(textWatcher)
            adopterEditText.addTextChangedListener(textWatcher)

            val personalData = sharedViewModel.personalData.value
            Log.d("ProcessAdopt", "image: ${personalData?.image}")


        }
    }

    private fun setupListener() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.processViewPager)

        submissionFragment.btnNext.setOnClickListener {
            val data = SubmissionData(
                reason =  submissionFragment.reasonEditText.text.toString(),
                hope =  submissionFragment.hopeEditText.text.toString(),
                maintain =  submissionFragment.maintainEditText.text.toString(),
                beside =  submissionFragment.besideEditText.text.toString(),
                vaccine =  submissionFragment.vaccineEditText.text.toString(),
                adopter =  submissionFragment.adopterEditText.text.toString()
            )
            sharedViewModel.submissionData.value = data
            viewPager?.currentItem = 3
        }
        submissionFragment.btnBack.setOnClickListener { viewPager?.currentItem = 1 }
    }

    private fun validateForm() {
        submissionFragment.apply {
            val reason = reasonEditText.text.toString()
            val hope = hopeEditText.text.toString()
            val maintain = maintainEditText.text.toString()
            val beside = besideEditText.text.toString()
            val vaccine = vaccineEditText.text.toString()
            val adopter = adopterEditText.text.toString()

            val isFormValid = reason.isNotEmpty() && hope.isNotEmpty() && maintain.isNotEmpty() && beside.isNotEmpty() && vaccine.isNotEmpty() && adopter.isNotEmpty()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _submissionFragment = null
    }

}