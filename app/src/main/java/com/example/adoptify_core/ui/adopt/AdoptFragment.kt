package com.example.adoptify_core.ui.adopt

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adoptify_core.databinding.FragmentAdoptBinding
import com.example.adoptify_core.ui.adopt.list.ListPageAdapter
import com.example.adoptify_core.ui.adopt.submission.SubmissionAdoptActivity
import com.google.android.material.tabs.TabLayoutMediator


class AdoptFragment : Fragment() {

    private var _adoptFragment: FragmentAdoptBinding? = null

    private val adoptFragment get() = _adoptFragment!!

    private val tabTitles = listOf(
        "Kucing",
        "Anjing"
    )

    private var logoutDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _adoptFragment = FragmentAdoptBinding.inflate(inflater, container, false)
        return _adoptFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adoptFragment.apply {
            val adapter = ListPageAdapter(requireActivity())
            viewPager.adapter = adapter
            val tabs = headerAdopt.tabs
            TabLayoutMediator(tabs, viewPager) {tab, position ->
                tab.text = tabTitles[position]
            }.attach()
        }

        setupView()
    }

    private fun setupView() {
        adoptFragment.headerAdopt.apply {
            icAdopt.setOnClickListener { startActivity(Intent(requireContext(), SubmissionAdoptActivity::class.java)) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adoptFragment = null
    }

}