package com.example.adoptify_core.ui.foster.form.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentReviewPersonalBinding
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.FormData
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class ReviewPersonalFragment : Fragment() {

    private var _reviewPersonal: FragmentReviewPersonalBinding? = null

    private val reviewPersonal get() = _reviewPersonal!!

    private val sessionViewModel: SessionViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var token: String = ""
    private var reqId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _reviewPersonal = FragmentReviewPersonalBinding.inflate(inflater, container, false)
        return _reviewPersonal?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        formDetailResult()
    }

    private fun observeData() {
        reqId = arguments?.getInt("REQ_ID")
        sessionViewModel.token.observe(viewLifecycleOwner) { token = it }
        fosterViewModel.getFormDetail(token, reqId!!)
    }

    private fun formDetailResult() {
        fosterViewModel.formDetail.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {showLoading(true)}
                is Resource.Success -> {
                    showLoading(false)
                    val formDetail = it.data.data?.first()
                    setupView(formDetail!!)
                }
                is Resource.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun setupView(data: FormData) {
        reviewPersonal.apply {
            name.text = data.name
            age.text = data.umur.toString()
            phone.text = data.noWa
            address.text = data.domisili
            job.text = data.pekerjaan
            range.text = data.rangePendapatan
            social.text = data.medsos
            val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.kartuIdentitas}"
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_place_holder)
                .into(cardIdentity)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        reviewPersonal.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        _reviewPersonal = null
        super.onDestroyView()
    }

}