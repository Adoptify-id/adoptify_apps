package com.example.adoptify_core.ui.foster.form.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adoptify_core.databinding.FragmentReviewGeneralBinding
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.FormData
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class ReviewGeneralFragment : Fragment() {

    private var _reviewGeneral: FragmentReviewGeneralBinding? = null

    private val reviewGeneral get() = _reviewGeneral!!

    private val sessionViewModel: SessionViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var token: String = ""
    private var reqId: Int? = null
    private var category: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _reviewGeneral = FragmentReviewGeneralBinding.inflate(inflater, container, false)
        return _reviewGeneral?.root
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
        reviewGeneral.apply {
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
        reviewGeneral.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _reviewGeneral = null
    }


}