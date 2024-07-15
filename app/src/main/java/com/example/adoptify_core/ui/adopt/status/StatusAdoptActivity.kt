package com.example.adoptify_core.ui.adopt.status

import android.app.Dialog
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityStatusAdoptBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.adopt.submission.SubmissionAdoptActivity
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.FormItem
import com.example.core.domain.model.DetailSubmissionData
import com.example.core.utils.ForceLogout
import com.example.core.utils.SessionViewModel
import com.example.core.utils.reduceImageFile
import com.example.core.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.Q)
class StatusAdoptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatusAdoptBinding

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var token: String = ""
    private var reqId: Int? = null

    private var progressDialog: Dialog? = null
    private var logoutDialog: Dialog? = null

    private var currentUriImage: Uri? = null
        set(value) {
            field = value
            showIndicatorSuccess(value != null && value.toString().isNotEmpty())
            validateForm()
        }

    private var currentImageTransfer: Uri? = null
        set(value)  {
            field = value
            showIndicatorSuccessTransfer(value != null && value.toString().isNotEmpty())
            validateForm()
        }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentUriImage = it
    }

    private val imageTransferLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        currentImageTransfer = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        updateResult()
        showDetailData()
        forceLogout()
        setupHeader()
        setupListener()
    }

    private fun observeData() {
        reqId = intent?.getIntExtra("REQ_ID", 0)
        sessionViewModel.token.observe(this) {
            token = it
        }

        adoptViewModel.getDetailSubmissionPet(token, reqId!!)
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


    private fun setupHeader() {
        binding.apply {
            statusAdopt.apply {
                line.setBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.primaryColor))
                icPersonalData.setImageDrawable(ContextCompat.getDrawable(this@StatusAdoptActivity, R.drawable.ic_personal_data_enable))
                line2.setBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.primaryColor))
                icSubmission.setImageDrawable(ContextCompat.getDrawable(this@StatusAdoptActivity, R.drawable.ic_submission_enable))
                line3.setBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.primaryColor))
                icStatus.setImageDrawable(ContextCompat.getDrawable(this@StatusAdoptActivity, R.drawable.ic_status_adopt_enable))
            }
            cardUpload.txtUpload.text = "Surat Komitmen"
            cardUploadTransfer.txtUpload.text = "Bukti Transaksi"
        }
    }

    private fun setupView(data: DetailSubmissionData) {
        binding.apply {
            if (data.statusReqId == 2 && data.statusPaymentId == 1 && data.statusPickupId == 1) {
                binding.indicatorAdopt.apply {
                    bgCard.setImageResource(R.drawable.bg_card_status)
                    bgIcon.setImageResource(R.drawable.bg_icon_status)
                    iconStatus.setImageResource(R.drawable.ic_status_accepted)
                    txtStatus.text = "Disetujui"
                    card.setCardBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.card_status_accepted))
                }
            } else if (data.statusReqId == 2 && data.statusPaymentId == 2 && data.statusPickupId == 2) {
                binding.indicatorAdopt.apply {
                    bgCard.setImageResource(R.drawable.bg_card_done)
                    bgIcon.setImageResource(R.drawable.bg_icon_done)
                    iconStatus.setImageResource(R.drawable.ic_status_done)
                    txtStatus.text = "Selesai"
                    card.setCardBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.card_status_done))
                    showIndicatorSuccess(true)
                    showIndicatorSuccessTransfer(true)
                    cardTransfer.card.visibility = View.GONE
                    btnDownload.visibility = View.GONE
                    btnClose.visibility = View.VISIBLE
                    cardUpload.root.isEnabled = false
                    cardUploadTransfer.root.isEnabled = false
                    btnClose.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
                    btnNext.visibility = View.GONE
                    btnBack.visibility = View.GONE

                }
            } else if (data.statusReqId == 3 && data.statusPaymentId == 1 && data.statusPickupId == 1)  {
                binding.indicatorAdopt.apply {
                    bgCard.setImageResource(R.drawable.bg_card_rejected)
                    bgIcon.setImageResource(R.drawable.bg_icon_rejected)
                    iconStatus.setImageResource(R.drawable.ic_status_rejected)
                    txtStatus.text = "Ditolak"
                    card.setCardBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.primaryColor))
                }
            }
            cardPengajuan.apply {
                dateRequest.text = parseDateToLong(data.reqDate)
                txtNamePet.text = data.namePet
                requestId.text = data.kodePengajuan
                cardIndicator.apply {
                    if (data.statusReqId == 2 && data.statusPaymentId == 1 && data.statusPickupId == 1) {
                        icIndicator.setImageResource(R.drawable.status_accepted)
                        txtIndicator.text = "Disetujui"
                        txtIndicator.setTextColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.text_card_accepted))
                        card.setCardBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.bg_card_accepted))
                    } else if (data.statusReqId == 2 && data.statusPaymentId == 2 && data.statusPickupId == 2) {
                        icIndicator.setImageResource(R.drawable.status_done)
                        txtIndicator.text = "Selesai"
                        txtIndicator.setTextColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.primary_color_foster))
                        card.setCardBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.bg_card_done))
                    } else if (data.statusReqId == 3 && data.statusPaymentId == 1 && data.statusPickupId == 1)  {
                        icIndicator.setImageResource(R.drawable.status_rejected)
                        txtIndicator.text = "Ditolak"
                        txtIndicator.setTextColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.text_card_rejected))
                        card.setCardBackgroundColor(ContextCompat.getColor(this@StatusAdoptActivity, R.color.bg_card_rejected))
                    }
                }
            }
        }
    }

    private fun showDetailData() {
        adoptViewModel.detailSubmission.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    val detail = it.data.data.first()
                    setupView(detail)
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("StatusAdoptActivity", "error: ${it.message}")
                }
            }
        }
    }

    private fun parseDateToLong(dateString: String?): String {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val date = originalFormat.parse(dateString)
            val targetFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            date?.let { targetFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    private fun setupListener() {
        binding.apply {
            cardTransfer.apply {
                val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                var clipData: ClipData

                icCopy.setOnClickListener {
                    val text = number.text
                    clipData = ClipData.newPlainText("text", text)
                    clipBoardManager.setPrimaryClip(clipData)
                    Toast.makeText(this@StatusAdoptActivity, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
                }
            }
            cardUpload.card.setOnClickListener { galleryLauncher.launch("image/*") }
            cardUploadTransfer.card.setOnClickListener { imageTransferLauncher.launch("image/*") }

            btnDownload.setOnClickListener {
                val url = "https://storage.googleapis.com/bucket-adoptify/skAdopter/SURAT%20KOMITMEN%20ADOPTER.docx"
                downloadPdf(url)
            }
            btnNext.setOnClickListener { updateHandler() }
            btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun validateForm() {
        val isFormValid = currentUriImage != null && currentImageTransfer != null
        binding.apply {
            btnNext.isEnabled = isFormValid
            btnNext.backgroundTintList = ContextCompat.getColorStateList(
                this@StatusAdoptActivity,
                if (isFormValid) R.color.primaryColor else R.color.btn_disabled
            )
            btnNext.setTextColor(
                if (isFormValid) resources.getColor(R.color.white) else resources.getColor(
                    R.color.black
                )
            )
        }
    }


    private fun updateHandler() {
        val imageSurat = currentUriImage?.let { uriToFile(it, this).reduceImageFile() }?.path
        val imageTransfer = currentImageTransfer?.let { uriToFile(it, this).reduceImageFile() }?.path

        val item = FormItem(
            suratKomitmen = imageSurat,
            transfer = imageTransfer,
            statusPaymentId = 3
        )

        adoptViewModel.updatePayment(token, reqId!!, item)
    }

    private fun updateResult() {
        adoptViewModel.updatePayment.observe(this) {
            when(it) {
                is Resource.Loading -> {showLoading(true)}
                is Resource.Success -> {
                    showLoading(false)
                    popUpDialog("Yeiy!", "Transaksi adopsi berhasil", "Selamat! Transaksi adopsi berhasil diajukan. Anda kini dapat melihat informasi pengajuan hewan" ,R.drawable.alert_success)
                    Log.d("StatusAdopt", "data: ${it.data}")
                }
                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Transaksi adopsi gagal", it.message ,R.drawable.alert_failed)
                    Log.d("StatusAdopt", "error: ${it.message}")
                }
            }
        }
    }

    private fun popUpDialog(title: String, desc: String, subDesc: String, image: Int) {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.alert_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val imageView = dialog.findViewById<ImageView>(R.id.img_alert)
            val titleText = dialog.findViewById<TextView>(R.id.title_alert)
            val descText = dialog.findViewById<TextView>(R.id.desc_alert)
            val subDescText = dialog.findViewById<TextView>(R.id.sub_desc_alert)
            val btnClose = dialog.findViewById<Button>(R.id.btnClose)

            imageView.setImageDrawable(ContextCompat.getDrawable(this@StatusAdoptActivity, image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener {
                startActivity(Intent(this@StatusAdoptActivity, SubmissionAdoptActivity::class.java))
                finish()
                dismiss()
            }
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBarDialog() else dismissProgressDialog()
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(this).apply {
                val view = LayoutInflater.from(this@StatusAdoptActivity).inflate(R.layout.dialog_progress, null)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.indeterminateTintList = ContextCompat.getColorStateList(this@StatusAdoptActivity, R.color.primary_color_foster)
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

    override fun onDestroy() {
        dismissProgressDialog()
        logoutDialog?.dismiss()
        super.onDestroy()
    }

    override fun onPause() {
        dismissProgressDialog()
        logoutDialog?.dismiss()
        super.onPause()
    }

    private fun showIndicatorSuccess(isSuccess: Boolean) {
        binding.cardUpload.successUpload.visibility =
            if (isSuccess) View.VISIBLE else View.GONE
        validateForm()
    }

    private fun showIndicatorSuccessTransfer(isSuccess: Boolean) {
        binding.cardUploadTransfer.successUpload.visibility =
            if (isSuccess) View.VISIBLE else View.GONE
        validateForm()
    }


    private fun downloadPdf(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Surat Komitmen Adopsi")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Surat Komitmen Adopsi.docx")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(this, "Download started...",Toast.LENGTH_SHORT).show()
    }

}