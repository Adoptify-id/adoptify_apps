package com.example.adoptify_core.ui.adopt.process.cat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentSecondSubmissionBinding
import com.example.adoptify_core.ui.adopt.SecondSubmissionData
import com.example.adoptify_core.ui.adopt.SharedDataViewModel


class SecondSubmissionFragment : Fragment() {

    private var _secondFragment: FragmentSecondSubmissionBinding? = null
    private lateinit var sharedViewModel: SharedDataViewModel
    private val secondFragment get() = _secondFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _secondFragment = FragmentSecondSubmissionBinding.inflate(inflater, container, false)
        return _secondFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]

        setupView()
        validateForm()
        setupListener()
    }

    private fun setupView() {
        secondFragment.apply {
            statusAdopt.line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            statusAdopt.icPersonalData.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_personal_data_enable))
            statusAdopt.line2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            statusAdopt.icSubmission.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_submission_enable))

            foodEditText.addTextChangedListener(textWatcher)
            merkEditText.addTextChangedListener(textWatcher)
            drinkEditText.addTextChangedListener(textWatcher)
            vaccineEditText.addTextChangedListener(textWatcher)
            medicineEditText.addTextChangedListener(textWatcher)
            moveEditText.addTextChangedListener(textWatcher)

            checkBoxPetCargo.setOnCheckedChangeListener { _, _ -> validateForm() }
            checkBoxTempatMakanMinum.setOnCheckedChangeListener { _, _ -> validateForm() }
            checkBoxLitterBox.setOnCheckedChangeListener { _, _ -> validateForm() }
            checkBoxMainanKucing.setOnCheckedChangeListener { _, _ -> validateForm() }
            checkBoxPerlengkapanMandi.setOnCheckedChangeListener { _, _ -> validateForm() }
            checkBoxBelumAda.setOnCheckedChangeListener { _, _ -> validateForm() }

            radioGroup.setOnCheckedChangeListener { _, _ -> validateForm() }
            radioPrivacy.setOnCheckedChangeListener { _, _ -> validateForm() }
            radioNews.setOnCheckedChangeListener { _, _ -> validateForm() }
        }
    }

    private fun setupListener() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.processViewPager)

        secondFragment.btnNext.setOnClickListener {
            val selectedCheckBoxes = getSelectedCheckBoxes()
            val radioCageOption = when (secondFragment.radioGroup.checkedRadioButtonId) {
                R.id.radio_all_time -> "Ya, sepanjang hari."
                R.id.radio_sometimes -> "Ya, hanya pada waktu-waktu tertentu saja."
                R.id.radio_no_activity -> "Tidak, kucing bebas bergerak atau beraktivitas di dalam rumah sepanjang hari."
                else -> secondFragment.otherEditText.text.toString()
            }
            val radioPrivacyOption = when (secondFragment.radioPrivacy.checkedRadioButtonId) {
                R.id.radio_yes -> "Ya"
                R.id.radio_no -> "Tidak"
                else -> ""
            }
            val radioNewsOption = when (secondFragment.radioNews.checkedRadioButtonId) {
                R.id.radio_news_yes -> "Ya"
                R.id.radio_news_no -> "Tidak"
                else -> ""
            }

            val data = SecondSubmissionData(
                food =  secondFragment.foodEditText.text.toString(),
                merk =  secondFragment.merkEditText.text.toString(),
                drink = secondFragment.drinkEditText.text.toString(),
                vaccine =  secondFragment.vaccineEditText.text.toString(),
                medicine =  secondFragment.medicineEditText.text.toString(),
                move =  secondFragment.moveEditText.text.toString(),
                selectedCheckBoxes= selectedCheckBoxes,
                radioCage =  radioCageOption,
                radioPrivacy =  radioPrivacyOption,
                radioNews =  radioNewsOption,
            )
            sharedViewModel.secondSubmissionData.value = data
            viewPager?.currentItem = 4
        }
        secondFragment.btnBack.setOnClickListener { viewPager?.currentItem = 2 }
    }

    private fun getSelectedCheckBoxes(): String {
        val selectedCheckBoxes = mutableListOf<String>()
        if (secondFragment.checkBoxPetCargo.isChecked) {
            selectedCheckBoxes.add("Pet Cargo")
        }
        if (secondFragment.checkBoxTempatMakanMinum.isChecked) {
            selectedCheckBoxes.add("Tempat makan & minum")
        }
        if (secondFragment.checkBoxLitterBox.isChecked) {
            selectedCheckBoxes.add("Litter box + Pasir/Soya Clump + Sekop")
        }
        if (secondFragment.checkBoxMainanKucing.isChecked) {
            selectedCheckBoxes.add("Mainan kucing")
        }
        if (secondFragment.checkBoxPerlengkapanMandi.isChecked) {
            selectedCheckBoxes.add("Perlengkapan mandi (Shampoo, Sisir, dll)")
        }
        if (secondFragment.checkBoxBelumAda.isChecked) {
            selectedCheckBoxes.add("Belum ada")
        }
        return selectedCheckBoxes.joinToString(", ")
    }

    private fun validateForm() {
        secondFragment.apply {
            val food = foodEditText.text.toString()
            val merk = merkEditText.text.toString()
            val drink = drinkEditText.text.toString()
            val vaccine = vaccineEditText.text.toString()
            val medicine = medicineEditText.text.toString()
            val move = moveEditText.text.toString()
            val other = otherEditText.text.toString()

            val isCheckBoxSelected = checkBoxPetCargo.isChecked || checkBoxTempatMakanMinum.isChecked || checkBoxLitterBox.isChecked || checkBoxMainanKucing.isChecked || checkBoxPerlengkapanMandi.isChecked || checkBoxBelumAda.isChecked

            val isRadioGroupCageSelected = radioGroup.checkedRadioButtonId != -1
            val isRadioPrivacySelected = radioPrivacy.checkedRadioButtonId != -1
            val isRadioNewsSelected = radioNews.checkedRadioButtonId != -1

            val isOtherTextFieldValid = isRadioGroupCageSelected || other.isNotEmpty()

            val isFormValid = food.isNotEmpty() && merk.isNotEmpty() && drink.isNotEmpty() && vaccine.isNotEmpty() && medicine.isNotEmpty() && move.isNotEmpty() && isCheckBoxSelected && isRadioPrivacySelected && isRadioNewsSelected && isOtherTextFieldValid
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
        _secondFragment = null
    }
}