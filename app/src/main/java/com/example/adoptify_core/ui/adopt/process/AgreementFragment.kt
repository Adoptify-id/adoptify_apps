package com.example.adoptify_core.ui.adopt.process

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentAgreementBinding


class AgreementFragment : Fragment() {

    private var _agreementFragment: FragmentAgreementBinding? = null

    private val agreementFragment get() = _agreementFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _agreementFragment = FragmentAgreementBinding.inflate(inflater, container, false)
        return _agreementFragment?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.processViewPager)

        agreementFragment.btnNext.setOnClickListener { viewPager?.currentItem = 1 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _agreementFragment = null
    }

}