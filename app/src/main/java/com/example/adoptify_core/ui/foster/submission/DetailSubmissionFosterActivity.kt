package com.example.adoptify_core.ui.foster.submission

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailSubmissionFosterBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.foster.FosterViewModel
import com.example.adoptify_core.ui.foster.form.ReviewFormActivity
import com.example.adoptify_core.ui.foster.upload.DetailPickupActivity
import com.example.adoptify_core.ui.foster.upload.UploadPickupActivity
import com.example.core.data.Resource
import com.example.core.domain.model.DetailItemSubmission
import com.example.core.utils.ForceLogout
import com.example.core.utils.SessionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailSubmissionFosterActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailSubmissionFosterBinding

    private val fosterViewModel: FosterViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var token: String = ""
    private var reqId: Int? = null

    private var progressDialog: Dialog? = null
    private var successDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSubmissionFosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        showDetailData()
        updateAdoptResult()
        updateStatusReqResult()
        updateStatusPaymentResult()
    }

    private fun observeData() {
        reqId = intent?.getIntExtra("REQ_ID", 0)
        sessionViewModel.token.observe(this) {
            token = it
        }
        fosterViewModel.detailSubmissionFoster(token, reqId!!)
    }

    private fun showDetailData() {
        fosterViewModel.detailSubmission.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    val detail = it.data.data.first()
                    setupView(detail)
                    Log.d("DetailSubmissionFoster", "data: $detail")
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("DetailSubmissionFoster", "error: ${it.message}")
                }
            }
        }
    }

    private fun setupView(data: DetailItemSubmission) {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.apply {
            header.icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            cardSubmission.apply {
                txtNamePet.text = data.namePet
                txtDescPet.text = data.descPet
                genderPet.text = data.gender
                agePet.text = "${data.umur} Bulan"
                rasPet.text = data.ras
                Glide.with(this@DetailSubmissionFosterActivity)
                    .load("https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}")
                    .placeholder(R.drawable.adopt_virtual_dog)
                    .into(imgPet)
            }
            requestId.text = data.kodePengajuan
            dateAdopt.text = parseDateToLong(data.reqDate)
            adopterName.text = data.name
            addressAdopter.text = data.domisili
            Glide.with(this@DetailSubmissionFosterActivity)
                .load("https://storage.googleapis.com/bucket-adoptify/imagesUser/${data.foto}")
                .placeholder(R.drawable.background_image_foster)
                .into(icFoster)
            cardDetailAdopter.apply {
                telpWA.text = data.noWa
                emailUser.text = data.email
                txtAddress.text = "Alamat Adopter"
                addressUser.text = data.domisili
            }
            cardUpload.cardKtp.setOnClickListener {
                val url =
                    "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.kartuIdentitas}"
                    downloadingImage("Kartu Identitas", url, "Kartu Identitas ${data.name}.jpg")
            }
            cardUpload.cardFormulir.setOnClickListener {
                val intent =
                    Intent(this@DetailSubmissionFosterActivity, ReviewFormActivity::class.java)
                intent.putExtra("REQ_ID", data.reqId)
                intent.putExtra("CATEGORY", data.kategori)
                startActivity(intent, options.toBundle())
            }

            cardUpload.cardKomitmen.visibility = View.VISIBLE
            cardUpload.cardTransfer.visibility = View.VISIBLE
            cardUpload.successUploadSurat.visibility = View.GONE
            cardUpload.successUploadTransfer.visibility = View.GONE

            if (data.statusReqId == 2 && data.statusPickupId == 1) {
                if (data.statusPaymentId == 1) {
                    cardUpload.cardKomitmen.visibility = View.VISIBLE
                    cardUpload.cardTransfer.visibility = View.VISIBLE
                } else if (data.statusPaymentId == 2) {
                    cardUpload.successUploadSurat.visibility = View.VISIBLE
                    cardUpload.successUploadTransfer.visibility = View.VISIBLE
                    cardUpload.cardKomitmen.setOnClickListener {
                        val url =
                            "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.suratKomitmen}"
                        downloadingImage("Surat Komitmen", url, "Surat Komitmen ${data.name}.jpg")
                    }
                    cardUpload.cardTransfer.setOnClickListener {
                        val url =
                            "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.transfer}"
                        downloadingImage("Bukti Transfer", url, "Bukti Transfer ${data.name}.jpg")
                    }
                } else if (data.statusPaymentId == 3) {
                    cardUpload.successUploadSurat.visibility = View.VISIBLE
                    cardUpload.successUploadTransfer.visibility = View.VISIBLE
                    cardUpload.cardKomitmen.setOnClickListener {
                        val url =
                            "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.suratKomitmen}"
                        downloadingImage("Surat Komitmen", url, "Surat Komitmen ${data.name}.jpg")
                    }
                    cardUpload.cardTransfer.setOnClickListener {
                        val url =
                            "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.transfer}"
                        downloadingImage("Bukti Transfer", url, "Bukti Transfer ${data.name}.jpg")
                    }
                }
            } else {
                cardUpload.cardKomitmen.visibility = View.GONE
                cardUpload.cardTransfer.visibility = View.GONE
                cardUpload.successUploadSurat.visibility = View.GONE
                cardUpload.successUploadTransfer.visibility = View.GONE
            }

            if (data.statusReqId == 2 && data.statusPaymentId == 2 && data.statusPickupId == 2) {
                cardUpload.cardKomitmen.visibility = View.VISIBLE
                cardUpload.cardTransfer.visibility = View.VISIBLE
                cardUpload.successUploadSurat.visibility = View.VISIBLE
                cardUpload.successUploadTransfer.visibility = View.VISIBLE
                cardUpload.cardKomitmen.setOnClickListener {
                    val url =
                        "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.suratKomitmen}"
                    downloadingImage("Surat Komitmen", url, "Surat Komitmen ${data.name}.jpg")
                }
                cardUpload.cardTransfer.setOnClickListener {
                    val url =
                        "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.transfer}"
                    downloadingImage("Bukti Transfer", url, "Bukti Transfer ${data.name}.jpg")
                }
            }

            if (data.statusReqId == 2 && data.statusPaymentId == 3 && data.statusPickupId == 1) {
                btnNext.setOnClickListener { fosterViewModel.updateStatusPayment(token, reqId!!, 2) }
                btnBack.isEnabled = false
            } else {
                btnNext.setOnClickListener {
                    fosterViewModel.updateAdopt(token, data.petId, true)
                    fosterViewModel.updateStatusReq(token, reqId!!, 2)
                }
                btnBack.setOnClickListener {
                    val dialog = BottomSheetDialog(this@DetailSubmissionFosterActivity)
                    val view =
                        layoutInflater.inflate(R.layout.modal_confirmation_cancel_adopt, null)
                    val btnClose = view.findViewById<Button>(R.id.btnBack)
                    val txtConfirm = view.findViewById<TextView>(R.id.confirm)
                    val btnCancel = view.findViewById<Button>(R.id.btnCancel)
                    txtConfirm.text = "Apakah anda yakin ingin \nmenolak data adopsi ini?"
                    btnCancel.text = "Tolak"
                    btnCancel.backgroundTintList = ContextCompat.getColorStateList(
                        this@DetailSubmissionFosterActivity, R.color.primary_color_foster
                    )
                    btnCancel.setOnClickListener { fosterViewModel.updateStatusReq(token, reqId!!, 3) }
                    btnClose.setOnClickListener { dialog.dismiss() }
                    dialog.setCancelable(false)
                    dialog.setContentView(view)
                    dialog.show()
                }
            }

            val status =
                data.statusReqId == 2 && data.statusPaymentId == 2 && data.statusPickupId == 1 || data.statusPickupId == 2 || data.statusReqId == 3 || data.statusReqId == 4
            btnClose.visibility = if (status) View.VISIBLE else View.GONE

            val statusAccepted =
                data.statusReqId == 2 && data.statusPaymentId == 2 && data.statusPickupId == 1
            if (statusAccepted) {
                btnClose.text = "Upload Bukti Pickup"
                btnClose.setOnClickListener {
                    val intent = Intent(
                        this@DetailSubmissionFosterActivity,
                        UploadPickupActivity::class.java
                    )
                    intent.putExtra("REQ_ID", data.reqId)
                    startActivity(intent, options.toBundle())
                }
            } else if (data.statusPickupId == 2) {
                btnClose.text = "Lihat Bukti Pickup"
                btnClose.setOnClickListener {
                    val intent = Intent(
                        this@DetailSubmissionFosterActivity,
                        DetailPickupActivity::class.java
                    )
                    intent.putExtra("REQ_ID", data.reqId)
                    startActivity(intent, options.toBundle())
                }
            } else {
                btnClose.setOnClickListener { finish() }
            }
            btnNext.visibility = if (status) View.GONE else View.VISIBLE
            btnBack.visibility = if (status) View.GONE else View.VISIBLE
        }
    }

    private fun updateAdoptResult() {
        fosterViewModel.update.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("DetailSubmissionFoster", "data: ${it.data}")
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("DetailSubmissionFoster", "error: ${it.message}")
                }
            }
        }
    }

    private fun updateStatusReqResult() {
        fosterViewModel.updateStatus.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    val data = it.data.data?.first()
                    val status = if (data?.statusReqId == 3) "menolak" else "menyetujui"
                    handleSuccess(status)
                    Log.d("DetailSubmissionFoster", "data: ${it.data}")
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("DetailSubmissionFoster", "error: ${it.message}")
                }
            }
        }
    }

    private fun updateStatusPaymentResult() {
        fosterViewModel.updatePayment.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    handleSuccess("menyetujui")
                    Log.d("UpdateStatusPayment", "data: ${it.data}")
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("UpdateStatusPayment", "error: ${it.message}")
                }
            }
        }
    }

    private fun parseDateToLong(dateString: String?): String {
        return try {
            val originalFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val date = originalFormat.parse(dateString)
            val targetFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            date?.let { targetFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    private fun showSuccessBottomSheet(status: String) {
        successDialog = BottomSheetDialog(this).apply {
            val view = layoutInflater.inflate(R.layout.modal_update_adopt, null)
            val txtUpload = view.findViewById<TextView>(R.id.descSuccess2)
            txtUpload.text = status
            setCancelable(false)
            setContentView(view)
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBarDialog() else dismissProgressDialog()
    }

    private fun handleSuccess(status: String) {
        showSuccessBottomSheet(status)
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 3000)
    }

    private fun progressBarDialog() {
        if (progressDialog == null) {
            progressDialog = Dialog(this).apply {
                val view = LayoutInflater.from(this@DetailSubmissionFosterActivity)
                    .inflate(R.layout.dialog_progress, null)
                setContentView(view)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.indeterminateTintList = ContextCompat.getColorStateList(
                    this@DetailSubmissionFosterActivity,
                    R.color.primary_color_foster
                )
            }
        }
        progressDialog?.show()
    }

    private fun downloadingImage(title: String, url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
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
        successDialog?.dismiss()
        super.onDestroy()
    }

    override fun onPause() {
        dismissProgressDialog()
        successDialog?.dismiss()
        super.onPause()
    }
}