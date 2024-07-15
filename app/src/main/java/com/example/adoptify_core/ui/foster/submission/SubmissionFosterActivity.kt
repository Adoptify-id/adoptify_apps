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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivitySubmissionFosterBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataSubmissionFoster
import com.example.core.ui.SubmissionFosterAdapter
import com.example.core.utils.ForceLogout
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SubmissionFosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubmissionFosterBinding

    private val sessionViewModel: SessionViewModel by viewModel()
    private val fosterViewModel: FosterViewModel by viewModel()

    private var token: String? = null
    private var userId: Int? = null
    private var listSubmissionFoster: List<DataSubmissionFoster> = listOf()

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmissionFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        submissionFosterResult()
        forceLogout()
        setupView()
    }

    private fun setupView() {
        binding.header.apply {
            txtBookmark.text = "Ajuan Adopsi"
            btnShortcut.visibility = View.GONE
            btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
        binding.radioFilterFoster.check(R.id.radio_semua)
        binding.radioFilterFoster.setOnCheckedChangeListener {_, checkedId ->
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

    private fun forceLogout() {
        ForceLogout.logoutLiveData.observe(this) {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        logoutDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.modal_session_expired)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //set width height card
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val btnLogin = findViewById<Button>(R.id.btnReload)
            btnLogin.backgroundTintList = ContextCompat.getColorStateList(this@SubmissionFosterActivity, R.color.primary_color_foster)
            btnLogin.setOnClickListener { navigateToLogin() }
            show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
//                        showRecyclerView()
                        filteredListSubmission(binding.radioFilterFoster.checkedRadioButtonId)
                        Log.d("SubmissionFoster", "data: $listSubmissionFoster")
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showContent(true)
                    showLoading(false)
                    Log.d("SubmissionFoster", "data: ${it.message}")
                }

                else -> {}
            }
        }
    }

    private fun showRecyclerView(filteredList: List<DataSubmissionFoster>) {
        binding.rvSubmission.apply {
            adapter = SubmissionFosterAdapter(filteredList) {
                val intent = Intent(
                    this@SubmissionFosterActivity,
                    DetailSubmissionFosterActivity::class.java
                )
                intent.putExtra("REQ_ID", it.reqId)
                startActivity(intent)
            }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SubmissionFosterActivity)
        }
    }

    private fun filteredListSubmission(checkedId: Int) {
        val filteredList = when(checkedId) {
            R.id.radio_semua -> listSubmissionFoster
            R.id.radio_disetujui -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 1 && it.statusPickupId == 1 }
            R.id.radio_ditolak -> listSubmissionFoster.filter { it.statusReqId == 3 }
            R.id.radio_belum_bayar -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 1 && it.statusPickupId == 1  }
            R.id.radio_sudah_bayar -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 3 && it.statusPickupId == 1 }
            R.id.radio_pickup -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 2 && it.statusPickupId == 1}
            R.id.radio_selesai -> listSubmissionFoster.filter { it.statusReqId == 2 && it.statusPaymentId == 2 && it.statusPickupId == 2 }
            else -> {throw IllegalArgumentException("")}
        }

        if (filteredList.isEmpty()) {
            showContent(true)
        } else {
            showContent(false)
        }

        showRecyclerView(filteredList)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showContent(isShowing: Boolean) {
        binding.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE
        binding.contentNull.txtTitle.setTextColor(resources.getColor(R.color.primary_color_foster))
        binding.contentNull.txtDesc.text =
            "Maaf, data pengajuan adopsi Anda tidak tersedia. Coba muat ulang atau periksa kembali nanti."

        binding.contentNull.btnClose.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        logoutDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        logoutDialog?.dismiss()
    }
}