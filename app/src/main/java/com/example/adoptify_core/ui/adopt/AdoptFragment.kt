package com.example.adoptify_core.ui.adopt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentAdoptBinding
import com.example.adoptify_core.ui.adopt.detail.DetailAdoptActivity
import com.example.adoptify_core.ui.adopt.list.ListPageAdapter
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.ui.PetItemAdapter
import com.example.core.ui.VirtualPetAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.viewModel


class AdoptFragment : Fragment() {

    private var _adoptFragment: FragmentAdoptBinding? = null

    private val adoptFragment get() = _adoptFragment!!

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private val tabTitles = listOf(
        "Kucing",
        "Anjing"
    )

    private var token = ""
    private var dataPet: List<DataAdopt> = listOf()

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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adoptFragment = null
    }

}