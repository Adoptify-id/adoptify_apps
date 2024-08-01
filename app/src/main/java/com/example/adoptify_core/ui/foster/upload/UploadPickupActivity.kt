package com.example.adoptify_core.ui.foster.upload

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityUploadPickupBinding
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.adoptify_core.ui.foster.submission.SubmissionFosterActivity
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.FormItem
import com.example.core.utils.SessionViewModel
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel

@RequiresApi(Build.VERSION_CODES.Q)
class UploadPickupActivity : BaseActivity() {

    private lateinit var binding: ActivityUploadPickupBinding

    private val fosterViewModel: FosterViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var currentUriImage: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
        try {
            binding.uploadPickup.setImageURI(currentUriImage)
            updateButtonState()
        } catch (e: Exception) {
            Log.d("UploadPickup", "error: ${e.message.toString()}")
        }
    }

    private var progressDialog: Dialog? = null
    private var successDialog: BottomSheetDialog? = null

    private var token: String? = null
    private var reqId: Int? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        observeData()
        uploadPickupResult()
        setupView()
        setupListener()
    }

    private fun observeData() {
        reqId = intent?.getIntExtra("REQ_ID", 0)
        sessionViewModel.token.observe(this) {
            token = it
        }
    }

    private fun setupView() {
        binding.apply {
            header.apply {
                txtBookmark.text = "Pengambilan"
                btnShortcut.visibility = View.GONE
                btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            }
            updateButtonState()
        }
    }

    private fun uploadPickupResult() {
        fosterViewModel.updatePickup.observe(this) {
            when (it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    handleSuccess()
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_to_upload_proof_pickup")
                    firebaseAnalytics.logEvent("upload_proof_pickup", bundle)
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("UploadPickupActivity", "error: ${it.message}")
                }
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            uploadPickup.setOnClickListener { galleryLauncher.launch("image/*") }
            btnChoose.setOnClickListener {updateHandler()}
        }
    }


    private fun updateHandler() {
        val imagePickup = currentUriImage?.let { uriToFile(it, this).reduceImageFile() }?.path
        val data = FormItem(
            statusPickupId = 2,
            buktiPickup = imagePickup
        )
        fosterViewModel.updateStatusPickup(token!!, reqId!!, data)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBarDialog() else dismissProgressDialog()
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(this).apply {
                val view = LayoutInflater.from(this@UploadPickupActivity)
                    .inflate(R.layout.dialog_progress, null)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.indeterminateTintList = ContextCompat.getColorStateList(
                    this@UploadPickupActivity,
                    R.color.primary_color_foster
                )
            }
        }
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun showSuccessBottomDialog() {
        successDialog = BottomSheetDialog(this).apply {
            val view = layoutInflater.inflate(R.layout.modal_update_adopt, null)
            val txtUpload = view.findViewById<TextView>(R.id.descSuccess2)
            val txtPickup = view.findViewById<TextView>(R.id.descSuccess3)
            txtUpload.text = "mengunggah"
            txtPickup.text = "bukti pickup hewan"
            setCancelable(false)
            setContentView(view)
            show()
        }
    }

    private fun handleSuccess() {
        showSuccessBottomDialog()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SubmissionFosterActivity::class.java))
            finish()
        }, 3000)
    }

    private fun updateButtonState() {
        binding.apply {
            val btnEnabled = currentUriImage != null
            btnChoose.isEnabled = btnEnabled
            btnChoose.backgroundTintList = ContextCompat.getColorStateList(
                this@UploadPickupActivity,
                if (btnEnabled) R.color.primary_color_foster else R.color.btn_disabled
            )
            btnChoose.setTextColor(
                if (btnEnabled) resources.getColor(R.color.white) else resources.getColor(
                    R.color.black
                )
            )
        }
    }

    override fun onDestroy() {
        successDialog?.dismiss()
        dismissProgressDialog()
        super.onDestroy()
    }

    override fun onPause() {
        successDialog?.dismiss()
        dismissProgressDialog()
        super.onPause()
    }
}