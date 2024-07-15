package com.example.adoptify_core.ui.adopt.submission

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivitySubmissionAdoptBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.data.Resource
import com.example.core.domain.model.SubmissionItem
import com.example.core.ui.ListSubmissionAdapter
import com.example.core.utils.ForceLogout
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SubmissionAdoptActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubmissionAdoptBinding

    private val sessionViewModel: SessionViewModel by viewModel()
    private val adoptViewModel: AdoptViewModel by viewModel()

    private var token: String? = null
    private var userId: Int? = null
    private var listSubmission: List<SubmissionItem> = listOf()

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmissionAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        listSubmissionResult()
        forceLogout()
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

    private fun showRecyclerView(filteredList: List<SubmissionItem>) {
        binding.rvSubmission.apply {
            adapter = ListSubmissionAdapter(filteredList) {
                val intent = Intent(this@SubmissionAdoptActivity, DetailSubmissionActivity::class.java)
                intent.putExtra("REQ_ID", it.reqId)
                startActivity(intent)
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

    override fun onDestroy() {
        super.onDestroy()
        logoutDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        logoutDialog?.dismiss()
    }
}