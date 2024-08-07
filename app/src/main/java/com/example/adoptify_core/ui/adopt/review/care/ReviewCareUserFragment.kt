package com.example.adoptify_core.ui.adopt.review.care

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentReviewCareUserBinding
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.FormData
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ReviewCareUserFragment : Fragment() {

    private var _reviewCare: FragmentReviewCareUserBinding? = null
    private val reviewCare get() = _reviewCare!!

    private val sessionViewModel: SessionViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var token: String = ""
    private var reqId: Int? = null
    private var category: String? = null
    private var isErrorDialogShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _reviewCare = FragmentReviewCareUserBinding.inflate(inflater, container, false)
        return _reviewCare?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        formDetailResult()
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
                    if (it.message.contains("Tidak ada koneksi internet.", ignoreCase = true)) {
                        showError(it.message)
                    }
                }
            }
        }
    }

    private fun observeData() {
        reqId = arguments?.getInt("REQ_ID")
        category = arguments?.getString("CATEGORY")
        sessionViewModel.token.observe(viewLifecycleOwner) { token = it }
        fosterViewModel.getFormDetail(token, reqId!!)
    }

    private fun setupView(data: FormData) {
        reviewCare.apply {
            needVaccine.text = data.ppk5
            medicine.text = data.ppk6
            privacy.text = data.ppk7
            move.text = data.ppk8
            call.text = data.ppk9
            tools.text = data.ppk10
            residence.text = data.rl1
            placed.text = data.rl2
            stay.text = data.rl3
            environment.text = data.rl4
            condition.text = data.rl5

            if (data.kategori == "Anjing") {
                txtPrivacy.text = "Apakah Anda bersedia memenuhi kebutuhan kesehatan rutin anjing seperti medical check up?"
                txtMove.text = "Apa yang akan Anda lakukan terhadap anjing yang Anda adopsi jika Anda harus pindah kota atau pergi liburan/perjalanan dinas ?"
                txtCall.text = "Apakah Anda bersedia untuk dihubungi sewaktu-waktu untuk menanyakan kabar dan informasi terbaru terkait anjing yang akan diadopsi?"
                tools.text = "Perlengkapan perawatan anjing yang sudah Anda dimiliki:"
                txtPlaced.text = "Dimanakah anjing yang akan Anda adopsi ditempatkan nantinya?"
                txtStay.text = "Jika mengadopsi anjing dari kami, dimana anjing akan tinggal?"
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        reviewCare.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        if (isErrorDialogShown) return
        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.network_error_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)
            val descText = dialog.findViewById<TextView>(R.id.desc)
            val btnClose = dialog.findViewById<Button>(R.id.btnRetry)
            descText.text = message
            btnClose.setOnClickListener {
                dialog.dismiss()
                isErrorDialogShown = false
                retryFetchingData()
            }
            show()
        }
        isErrorDialogShown = true
    }

    private fun retryFetchingData() {
        showLoading(true)
        fosterViewModel.getFormDetail(token, reqId!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _reviewCare = null
    }

}