package com.example.adoptify_core.ui.adopt.process

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentPersonalDataBinding

class PersonalDataFragment : Fragment() {

    private var _personalData: FragmentPersonalDataBinding? = null

    private val personalData get() = _personalData!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _personalData = FragmentPersonalDataBinding.inflate(inflater, container, false)
        return _personalData?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListener()
    }

    private fun setupView() {
        personalData.statusAdopt.line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        personalData.statusAdopt.icPersonalData.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_personal_data_enable))
    }

    private fun setupListener() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.processViewPager)

        personalData.btnNext.setOnClickListener { viewPager?.currentItem = 2 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _personalData = null
    }
}