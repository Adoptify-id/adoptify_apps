package com.example.adoptify_core.ui.foster.submission

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivitySubmissionFosterBinding
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataSubmissionFoster
import com.example.core.ui.SubmissionFosterAdapter
import com.example.core.utils.SessionViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel

class SubmissionFosterActivity : BaseActivity() {

    private lateinit var binding: ActivitySubmissionFosterBinding

    private val sessionViewModel: SessionViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var token: String? = null
    private var userId: Int? = null
    private var listSubmissionFoster: List<DataSubmissionFoster> = listOf()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var isErrorDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmissionFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        shimmerFrameLayout = binding.shimmerLayout
        observeData()
        submissionFosterResult()
        setupView()
    }

    private fun setupView() {
        binding.header.apply {
            txtBookmark.text = "Ajuan Adopsi"
            btnShortcut.visibility = View.GONE
            btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
        binding.radioFilterFoster.check(R.id.radio_semua)
        binding.radioFilterFoster.setOnCheckedChangeListener { _, checkedId ->
            filteredListSubmission(checkedId)
        }
    }

    private fun observeData() {
        sessionViewModel.token.observe(this) {
            token = it
            if (token != null && userId != null) {
                fosterViewModel.getListSubmissionFoster(token!!, userId!!)
            }
        }

        sessionViewModel.userId.observe(this) {
            userId = it
            if (token != null && userId != null) {
                fosterViewModel.getListSubmissionFoster(token!!, userId!!)
            }
        }
    }

    private fun submissionFosterResult() {
        fosterViewModel.listSubmission.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showContent(true)
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    showContent(false)
                    listSubmissionFoster = it.data
                    if (listSubmissionFoster.isNotEmpty()) {
                        filteredListSubmission(binding.radioFilterFoster.checkedRadioButtonId)
                        Log.d("SubmissionFoster", "data: $listSubmissionFoster")
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showContent(true)
                    showLoading(false)
                    if (it.message.contains("Tidak ada koneksi internet.", ignoreCase = true)) {
                        showError(it.message)
                    }
                    Log.d("SubmissionFoster", "data: ${it.message}")
                }

                else -> {}
            }
        }
    }

    private fun showRecyclerView(filteredList: List<DataSubmissionFoster>) {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.rvSubmission.apply {
            adapter = SubmissionFosterAdapter(filteredList) {
                val intent = Intent(
                    this@SubmissionFosterActivity,
                    DetailSubmissionFosterActivity::class.java
                )
                intent.putExtra("REQ_ID", it.reqId)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_to_detail_submission_foster")
                firebaseAnalytics.logEvent("navigate_to_detail_submission_foster", bundle)
                startActivity(intent, options.toBundle())
            }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SubmissionFosterActivity)
        }
    }

    private fun filteredListSubmission(checkedId: Int) {
        val filteredList = when (checkedId) {
            R.id.radio_semua -> listSubmissionFoster
            R.id.radio_disetujui -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 1 && it.statusPickupId == 1 }
            R.id.radio_ditolak -> listSubmissionFoster.filter { it.statusReqId == 3 }
            R.id.radio_belum_bayar -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 1 && it.statusPickupId == 1 }
            R.id.radio_sudah_bayar -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 3 && it.statusPickupId == 1 }
            R.id.radio_pickup -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 2 && it.statusPickupId == 1 }
            R.id.radio_selesai -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 2 && it.statusPickupId == 2 }
            else -> {
                throw IllegalArgumentException("")
            }
        }

        if (filteredList.isEmpty()) {
            showContent(true)
        } else {
            showContent(false)
        }

        showRecyclerView(filteredList)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            binding.rvSubmission.visibility = View.GONE
            showContent(false)
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            binding.rvSubmission.visibility = View.VISIBLE
        }
    }

    private fun showContent(isShowing: Boolean) {
        binding.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE
        binding.contentNull.txtTitle.setTextColor(resources.getColor(R.color.primary_color_foster))
        binding.contentNull.txtDesc.text =
            "Maaf, data pengajuan adopsi Anda tidak tersedia. Coba muat ulang atau periksa kembali nanti."

        binding.contentNull.btnClose.visibility = View.GONE
    }

    private fun showError(message: String) {
        if (isErrorDialogShown) return
        val dialog = Dialog(this)
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
        fosterViewModel.getListSubmissionFoster(token!!, userId!!)
    }
}