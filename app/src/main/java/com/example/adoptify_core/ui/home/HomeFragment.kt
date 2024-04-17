package com.example.adoptify_core.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentHomeBinding
import com.example.adoptify_core.ui.adopt.AdoptFragment
import com.example.adoptify_core.ui.medical.MedicalRecordActivity
import com.example.adoptify_core.ui.medical.MedicalRecordFragment
import com.example.adoptify_core.ui.virtual.main.VirtualPetFragment


class HomeFragment : Fragment() {

    private var _homeFragment: FragmentHomeBinding? = null
    private val homeFragment get() = _homeFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _homeFragment = FragmentHomeBinding.inflate(inflater, container, false)
        return _homeFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeFragment.btnMedicalRecord.setOnClickListener { startActivity(Intent(requireContext(), MedicalRecordActivity::class.java)) }
//        homeFragment.btnVirtualPet.setOnClickListener { startActivity(Intent(requireContext(), VirtualPetFragment::class.java)) }
//        homeFragment.cardDog.virtualDog.setOnClickListener { startActivity(Intent(requireContext(), AdoptFragment::class.java)) }
//        homeFragment.cardCat.virtualCat.setOnClickListener { startActivity(Intent(requireContext(), AdoptFragment::class.java)) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _homeFragment = null
    }

}