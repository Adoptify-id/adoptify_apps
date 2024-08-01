package com.example.adoptify_core.ui.adopt.submission

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivitySubmissionAdoptBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.SubmissionItem
import com.example.core.ui.ListSubmissionAdapter
import com.example.core.utils.SessionViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel

class SubmissionAdoptActivity : BaseActivity() {

    private lateinit var binding: ActivitySubmissionAdoptBinding

    private val sessionViewModel: SessionViewModel by viewModel()
    private val adoptViewModel: AdoptViewModel by viewModel()

    private var token: String? = null
    private var userId: Int? = null
    private var listSubmission: List<SubmissionItem> = listOf()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmissionAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        observeData()
        listSubmissionResult()
        setupView()
    }

    private fun observeData() {
        sessionViewModel.token.observe(this) {
            token = it
            if (token != null && userId != null) {
                adoptViewModel.getListSubmissionPet(token!!, userId!!)
            }
        }

        sessionViewModel.userId.observe(this) {
            userId = it
            if (token != null && userId != null) {
                adoptViewModel.getListSubmissionPet(token!!, userId!!)
            }
        }
    }

    private fun listSubmissionResult() {
        adoptViewModel.listSubmission.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showContent(true)
                    showLoading(true)
                }

                is Resource.Success -> {
                    showContent(false)
                    showLoading(false)
                    listSubmission = it.data
                    if (listSubmission.isNotEmpty()) {
                        filterListSubmission(binding.radioFilter.checkedRadioButtonId)
                        Log.d("ListSubmissionPet", "data: ${it.data}")
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    showContent(true)
                    Log.d("ListSubmissionPet", "data: ${it.message}")
                }
            }
        }
    }

    private fun showRecyclerView(filteredList: List<SubmissionItem>) {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.rvSubmission.apply {
            adapter = ListSubmissionAdapter(filteredList) {
                val intent = Intent(this@SubmissionAdoptActivity, DetailSubmissionActivity::class.java)
                intent.putExtra("REQ_ID", it.reqId)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_to_detail_submission")
                firebaseAnalytics.logEvent("navigate_to_detail_submission", bundle)
                startActivity(intent, options.toBundle())
            }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SubmissionAdoptActivity)
        }
    }

    private fun showContent(isShowing: Boolean) {
        binding.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE
        binding.contentNull.txtDesc.text = "Maaf, data ajuan adopsi Anda tidak tersedia. Coba muat ulang atau periksa kembali nanti."

        binding.contentNull.btnClose.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupView() {
        binding.apply {
            header.txtBookmark.text = "Ajuan Adopsi"
            header.btnShortcut.visibility = View.GONE
            header.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            radioFilter.check(R.id.radio_semua)

            radioFilter.setOnCheckedChangeListener{_, checkedId ->
                filterListSubmission(checkedId)
            }
        }
    }

    private fun filterListSubmission(checkedId: Int) {
        val filteredList = when(checkedId) {
            R.id.radio_semua -> listSubmission
            R.id.radio_disetujui -> listSubmission.filter { it.statusReqId == 2 && it.statusPaymentId == 1 && it.statusPickupId == 1 }
            R.id.radio_ditolak -> listSubmission.filter { it.statusReqId == 3 }
            R.id.radio_belum_bayar -> listSubmission.filter { it.statusReqId == 2 && it.statusPaymentId == 1 && it.statusPickupId == 1  }
            R.id.radio_sudah_bayar -> listSubmission.filter { it.statusReqId == 2 && it.statusPaymentId == 3 && it.statusPickupId == 1 }
            R.id.radio_pickup -> listSubmission.filter { it.statusReqId == 2 && it.statusPaymentId == 2 && it.statusPickupId == 1}
            R.id.radio_selesai -> listSubmission.filter { it.statusReqId == 2 && it.statusPaymentId == 2 && it.statusPickupId == 2 }
            R.id.radio_cancel -> listSubmission.filter { it.statusReqId == 4 }
            else -> {throw IllegalArgumentException("")}
        }

        if (filteredList.isEmpty()) {
            showContent(true)
        } else {
            showContent(false)
        }

        showRecyclerView(filteredList)
    }
}