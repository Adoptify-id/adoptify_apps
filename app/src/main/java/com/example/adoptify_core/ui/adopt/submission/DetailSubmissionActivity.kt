package com.example.adoptify_core.ui.adopt.submission

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailSubmissionBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.adopt.review.ReviewFormUserActivity
import com.example.adoptify_core.ui.adopt.status.StatusAdoptActivity
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.data.Resource
import com.example.core.domain.model.DetailSubmissionData
import com.example.core.utils.ForceLogout
import com.example.core.utils.SessionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailSubmissionActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailSubmissionBinding

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var token: String = ""
    private var reqId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        observeData()
        showDetailData()
        cancelAdoptResult()
    }

    private fun observeData() {
       reqId = intent?.getIntExtra("REQ_ID", 0)
        sessionViewModel.token.observe(this) {
            token = it
        }

        adoptViewModel.getDetailSubmissionPet(token, reqId!!)
    }

    private fun setupView(data: DetailSubmissionData) {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.apply {
            cardSubmission.apply {
                txtNamePet.text = data.namePet
                genderPet.text = data.gender
                agePet.text = "${data.umur} bulan"
                rasPet.text = data.ras
                Glide.with(this@DetailSubmissionActivity)
                    .load("https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}")
                    .placeholder(R.drawable.adopt_virtual_dog)
                    .into(imgPet)
            }
            requestId.text = data.kodePengajuan
            dateAdopt.text = parseDateToLong(data.reqDate)
            fosterName.text = data.fullName
            Glide.with(this@DetailSubmissionActivity)
                .load("https://storage.googleapis.com/bucket-adoptify/imagesUser/${data.foto}")
                .placeholder(R.drawable.background_image_foster)
                .into(icFoster)
            cardDetailFoster.apply {
                telpWA.text = data.noTelp
                emailUser.text = data.email
                addressUser.text = "${data.alamat}, ${data.provinsi}, ${data.kodePos}"
            }
            cardUpload.cardKtp.setOnClickListener {
                val url = "https://storage.googleapis.com/bucket-adoptify/imagesReqPet/${data.kartuIdentitas}"
                downloadingImage("Kartu Identitas", url, "Kartu Identitas ${data.fullName}.jpg")
            }
            cardUpload.cardFormulir.setOnClickListener {
                val intent = Intent(this@DetailSubmissionActivity, ReviewFormUserActivity::class.java)
                intent.putExtra("REQ_ID", data.reqId)
                intent.putExtra("CATEGORY", data.kategori)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_to_review_form")
                firebaseAnalytics.logEvent("navigate_to_review_form", bundle)
                startActivity(intent, options.toBundle())
            }
            header.icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            val status = data.statusReqId == 4
            btnClose.visibility = if (status) View.VISIBLE else View.GONE
            btnClose.setOnClickListener { finish() }
            btnNext.visibility = if (status) View.GONE else View.VISIBLE
            btnBack.visibility = if (status) View.GONE else View.VISIBLE
            btnNext.isEnabled = data.statusReqId == 2 || data.statusReqId == 3
            btnBack.isEnabled =  data.statusReqId == 1
            btnNext.backgroundTintList = ContextCompat.getColorStateList(
                this@DetailSubmissionActivity,
                if (btnNext.isEnabled) R.color.primaryColor else R.color.btn_disabled
            )
            btnNext.setOnClickListener {
                val intent = Intent(this@DetailSubmissionActivity, StatusAdoptActivity::class.java)
                intent.putExtra("REQ_ID", reqId)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_to_status_submission")
                firebaseAnalytics.logEvent("navigate_to_status_submission", bundle)
                startActivity(intent, options.toBundle())
                finish()

            }
            btnBack.setOnClickListener {
                val dialog = BottomSheetDialog(this@DetailSubmissionActivity)
                val view = layoutInflater.inflate(R.layout.modal_confirmation_cancel_adopt, null)
                val btnClose = view.findViewById<Button>(R.id.btnBack)
                val btnCancel = view.findViewById<Button>(R.id.btnCancel)
                btnCancel.setOnClickListener {
                    adoptViewModel.cancelAdopt(token, reqId!!, 4)
                    finish()
                }
                btnClose.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.setCancelable(false)
                dialog.setContentView(view)
                dialog.show()
            }
        }
    }

    private fun showDetailData() {
        adoptViewModel.detailSubmission.observe(this) {
            when(it) {
                is Resource.Loading -> {showLoading(true)}
                is Resource.Success -> {
                    showLoading(false)
                    val detailSubmission = it.data.data.first()
                    setupView(detailSubmission)
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("DetailSubmissionPet", "error: ${it.message}")
                }
            }
        }
    }

    private fun cancelAdoptResult() {
        adoptViewModel.cancelAdopt.observe(this) {
            when(it) {
                is Resource.Loading -> {showLoading(true)}
                is Resource.Success -> {
                    showLoading(false)
                    popUpDialog("Yeiy!", "Data adopsi berhasil dibatalkan", "Selamat! Transaksi adopsi berhasil dibatalkan. Anda kini dapat mengajukan hewan yang lain" ,R.drawable.alert_success)
                    Log.d("DetailSubmission", "succcess : ${it.data}")
                }
                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Data adopsi gagal dibatalkan", it.message ,R.drawable.alert_failed)
                    Log.d("DetailSubmissionPet", "error: ${it.message}")
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

    private fun downloadingImage(title: String, url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
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

            imageView.setImageDrawable(ContextCompat.getDrawable(this@DetailSubmissionActivity, image))
            titleText.text = title
            descText.text = desc
            subDescText.text = subDesc
            btnClose.setOnClickListener {
                startActivity(Intent(this@DetailSubmissionActivity, SubmissionAdoptActivity::class.java))
                finish()
                dismiss()
            }
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}