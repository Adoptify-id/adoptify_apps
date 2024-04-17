package com.example.adoptify_core.ui.medical.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentRecordBinding


class RecordFragment : Fragment() {

    private var _recordFragment: FragmentRecordBinding? = null
    private val recordFragment get() = _recordFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _recordFragment = FragmentRecordBinding.inflate(inflater, container, false)
        return _recordFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordFragment.header.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _recordFragment = null
    }



}