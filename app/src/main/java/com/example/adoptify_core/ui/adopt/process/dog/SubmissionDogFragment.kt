package com.example.adoptify_core.ui.adopt.process.dog

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
import com.example.adoptify_core.databinding.FragmentSubmissionDogBinding
import com.example.adoptify_core.ui.adopt.SharedDataViewModel
import com.example.adoptify_core.ui.adopt.SubmissionData


class SubmissionDogFragment : Fragment() {

    private var _submissionDog: FragmentSubmissionDogBinding? = null

    private val submissionDog get() = _submissionDog!!
    private lateinit var sharedViewModel: SharedDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _submissionDog = FragmentSubmissionDogBinding.inflate(inflater, container, false)
        return _submissionDog?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]

        setupView()
        validateForm()
        setupListener()
    }

    private fun setupView() {
        submissionDog.apply {
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
        val viewPager = activity?.findViewById<ViewPager2>(R.id.processDogViewPager)

        submissionDog.btnNext.setOnClickListener {
            val data = SubmissionData(
                reason =  submissionDog.reasonEditText.text.toString(),
                hope =  submissionDog.hopeEditText.text.toString(),
                maintain =  submissionDog.maintainEditText.text.toString(),
                beside =  submissionDog.besideEditText.text.toString(),
                vaccine =  submissionDog.vaccineEditText.text.toString(),
                adopter =  submissionDog.adopterEditText.text.toString()
            )
            sharedViewModel.submissionData.value = data
            viewPager?.currentItem = 3
        }
        submissionDog.btnBack.setOnClickListener { viewPager?.currentItem = 1 }
    }

    private fun validateForm() {
        submissionDog.apply {
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
            btnNext.setTextColor(if (isFormValid) resources.getColor(R.color.white) else resources.getColor(
                R.color.black))
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {validateForm()}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _submissionDog = null
    }
}