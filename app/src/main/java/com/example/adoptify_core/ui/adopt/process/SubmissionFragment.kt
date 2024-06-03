package com.example.adoptify_core.ui.adopt.process

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentSubmissionBinding


class SubmissionFragment : Fragment() {

    private var _submissionFragment: FragmentSubmissionBinding? = null

    private val submissionFragment get() = _submissionFragment!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _submissionFragment = FragmentSubmissionBinding.inflate(inflater, container, false)
        return _submissionFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        submissionFragment.statusAdopt.line.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        submissionFragment.statusAdopt.icPersonalData.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_personal_data_enable))
        submissionFragment.statusAdopt.line2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        submissionFragment.statusAdopt.icSubmission.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_submission_enable))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _submissionFragment = null
    }

}