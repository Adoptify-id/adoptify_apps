package com.example.adoptify_core.ui.adopt.review.general

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
import com.example.adoptify_core.databinding.FragmentReviewGeneralUserBinding
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.FormData
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class ReviewGeneralUserFragment : Fragment() {

    private var _generalFragment: FragmentReviewGeneralUserBinding? = null
    private val generalFragment get() = _generalFragment!!

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
       _generalFragment = FragmentReviewGeneralUserBinding.inflate(inflater, container, false)
        return _generalFragment?.root
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
        generalFragment.apply {
            reason.text = data.umum1
            hope.text = data.umum2
            experience.text = data.umum3
            besides.text = data.umum4
            vaccine.text = data.umum5
            sterile.text = data.umum6
            food.text = data.ppk1
            merk.text = data.ppk2
            drink.text = data.ppk3
            cage.text = data.ppk4

            if (data.kategori == "Anjing") {
                txtHope.text = "Bagaimana jika anjing yang diadopsi tidak sesuai dengan harapan?"
                txtExperience.text = "Apakah anda sedang/pernah memelihara anjing sebelumnya?"
                txtBesides.text = "Apakah anda mempunyai hewan peliharaan selain anjing?"
                txtVaccine.text = "Jika anda sedang merawat anjing, apakah anjing tersebut sudah di vaksin?"
                txtSterile.text = " Apakah lingkungan mendukung keberadaan anjing?"
                txtFood.text = "Jenis makanan apa yang akan kamu berikan?"
                txtMerk.text = "Apakah ada merk tertentu jika makanan anjing yang diberikan adalah makanan instant?"
                txtDrink.text = "Bersediakah kamu memberikan kebutuhan pakan dengan tepat waktu pada anjing?"
                txtCage.text = "Apakah kamu berencana untuk memasukkan anjing ke dalam kandang?"
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        generalFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
        _generalFragment = null
    }

}