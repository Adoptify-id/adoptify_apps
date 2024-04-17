package com.example.adoptify_core.ui.welcome.first

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentFirstWelcomeBinding


class FirstWelcomeFragment : Fragment() {

    private var _firstFragment: FragmentFirstWelcomeBinding? = null

    private val firstFragment get() = _firstFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _firstFragment = FragmentFirstWelcomeBinding.inflate(inflater, container, false)
        return _firstFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.welcomeViewPager)

        firstFragment.btnNext.setOnClickListener { viewPager?.currentItem = 1 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _firstFragment = null
    }


}