package com.example.adoptify_core.ui.welcome.third

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentThirdWelcomeBinding


class ThirdWelcomeFragment : Fragment() {

    private var _thirdFragment: FragmentThirdWelcomeBinding? = null
    private val thirdFragment get() = _thirdFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _thirdFragment = FragmentThirdWelcomeBinding.inflate(inflater,container , false)
        return _thirdFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.welcomeViewPager)

        thirdFragment.btnNext.setOnClickListener { viewPager?.currentItem = 3 }
        thirdFragment.btnBack.setOnClickListener { viewPager?.currentItem = 1 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _thirdFragment = null
    }

}