package com.example.adoptify_core.ui.medical

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentMedicalRecordBinding
import com.example.adoptify_core.ui.medical.record.RecordFragment


class MedicalRecordFragment : Fragment() {

    private var _medicalFragment: FragmentMedicalRecordBinding? = null
    private val medicalFragment get() = _medicalFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _medicalFragment = FragmentMedicalRecordBinding.inflate(inflater, container, false)
        return _medicalFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicalFragment.cardMedical.btnMedical.setOnClickListener {
            val fragment = RecordFragment()
            val fragmentManager = requireActivity().supportFragmentManager.beginTransaction()
            fragmentManager.replace(R.id.fragment_container, fragment)
            fragmentManager.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _medicalFragment = null
    }
}