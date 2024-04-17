package com.example.adoptify_core.ui.welcome.second

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentSecondWelcomeBinding


class SecondWelcomeFragment : Fragment() {

    private var _secondFragment : FragmentSecondWelcomeBinding? = null
    private val secondFragment get() = _secondFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _secondFragment = FragmentSecondWelcomeBinding.inflate(inflater, container, false)
        return _secondFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.welcomeViewPager)

        secondFragment.btnNext.setOnClickListener { viewPager?.currentItem = 2 }
        secondFragment.btnBack.setOnClickListener { viewPager?.currentItem = 0 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _secondFragment = null
    }

}