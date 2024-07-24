package com.example.adoptify_core.ui.welcome.fourth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentFourthWelcomeBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity


class FourthWelcomeFragment : Fragment() {

    private var _fourthFragment : FragmentFourthWelcomeBinding? = null
    private val fourthFragment get() = _fourthFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fourthFragment = FragmentFourthWelcomeBinding.inflate(inflater, container, false)
        return _fourthFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.welcomeViewPager)

        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        fourthFragment.btnNext.setOnClickListener { startActivity(Intent(requireContext(), LoginActivity::class.java), options.toBundle()) }
        fourthFragment.btnBack.setOnClickListener { viewPager?.currentItem = 2 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fourthFragment = null
    }

}