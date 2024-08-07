package com.example.adoptify_core.ui.adopt.detail

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
import com.bumptech.glide.Glide
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityDetailAdoptBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.adopt.process.cat.ProcessAdoptActivity
import com.example.adoptify_core.ui.adopt.process.dog.ProcessAdoptDogActivity
import com.example.adoptify_core.ui.bookmark.BookmarkViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.utils.DataMapper
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel

class DetailAdoptActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailAdoptBinding

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val bookmarkViewModel: BookmarkViewModel by viewModel()

    private var token = ""
    private var petId: Int? = null
    private var isFavorite = false
    private var currentData: DataAdopt? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var isErrorDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        shimmerFrameLayout = binding.shimmerLayout
        val transitionName = intent.getStringExtra("TRANSITION_NAME")
        binding.imagePet.transitionName = transitionName

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(binding.imagePet)
            duration = 500L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(binding.imagePet)
            duration = 500L
        }

        initData()
        getDetailResult()
        setupListener()
    }

    private fun initData() {
        token = intent?.getStringExtra("TOKEN") ?: ""
        val petId = intent.getIntExtra("PET_ID", 0)

        adoptViewModel.getDetailPet(token, petId)

        petId.let { id ->
            bookmarkViewModel.isFavorite(id).observe(this) { favorite ->
                isFavorite = favorite
                setIconButton(isFavorite)
            }
        }
    }

    private fun setupView(data: DataAdopt?) {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        currentData = data
        val imageUrl = if (data?.fotoPet == null) {
            null
        } else {
            "https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}"
        }
        val imageProfile = if (data?.foto == null) {
            null
        } else {
            "https://storage.googleapis.com/bucket-adoptify/imagesUser/${data.foto}"
        }

        binding.apply {
            Glide.with(this@DetailAdoptActivity)
                .load(imageUrl ?: R.drawable.detail_pet)
                .placeholder(R.drawable.detail_pet)
                .into(imagePet)

            namePet.text = data?.namePet?.split(" ")?.joinToString(separator = " ") { it.capitalize() }
            genderPet.text = data?.gender
            agePet.text = if (data?.ageType.isNullOrEmpty()) "${data?.umur} Bulan" else "${data?.umur} ${data?.ageType}"
            rasPet.text = data?.ras
            txtDescPet.text = data?.descPet
            txtIdPet.text = "#${data?.petId}"
            petId = data?.petId
            txtLocation.text = if (data?.alamat.isNullOrEmpty() || data?.provinsi.isNullOrEmpty()) "Lokasi tidak disetel" else "${data?.alamat}, ${data?.provinsi}"
            txtFosterName.text = data?.fullName?.split(" ")?.joinToString(separator = " ") { it.capitalize() } ?: data?.username?.split(" ")?.joinToString { it.capitalize() }
            Glide.with(this@DetailAdoptActivity)
                .load(imageProfile ?: R.drawable.ic_foster)
                .placeholder(R.drawable.ic_foster)
                .into(icFoster)
            btnAdopt.setOnClickListener {
                if (data?.kategori == "Kucing") {
                    val intent = Intent(this@DetailAdoptActivity, ProcessAdoptActivity::class.java)
                    intent.putExtra("PET_ID", data.petId)
                    startActivity(intent, options.toBundle())
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_process_adopt_cat")
                    firebaseAnalytics.logEvent("navigate_to_process_adopt_cat", bundle)
                } else {
                    val intent = Intent(this@DetailAdoptActivity, ProcessAdoptDogActivity::class.java)
                    intent.putExtra("PET_ID", data?.petId)
                    startActivity(intent, options.toBundle())
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_process_adopt_dog")
                    firebaseAnalytics.logEvent("navigate_to_process_adopt_dog", bundle)
                }
            }
        }
    }

    private fun getDetailResult() {
        adoptViewModel.detail.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("DetailAdopt", "result: ${it.data}")
                    val detailData = it.data.data?.first()
                    setupView(detailData)
                }

                is Resource.Error -> {
                    showLoading(false)
                    if (it.message.contains("Tidak ada koneksi internet.", ignoreCase = true)) {
                        showError(it.message)
                    }
                    Log.d("DetailAdopt", "error: ${it.message}")
                }
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            icFavorite.setOnClickListener {
                isFavorite = !isFavorite
                val changeDrawable = if (isFavorite) R.drawable.ic_love else R.drawable.ic_love_grey
                binding.icFavorite.setImageResource(changeDrawable)
                currentData.let {
                    val pets = DataAdopt(
                        petId = it!!.petId,
                        namePet = it.namePet,
                        fotoPet = it.fotoPet,
                        gender = it.gender,
                        umur = it.umur,
                        ras = it.ras,
                        alamat = it.alamat,
                        provinsi = it.provinsi,
                        isAdopt = it.isAdopt,
                        ageType = it.ageType
                    )
                    val data = DataMapper.petsEntityToPets(pets, isFavorite)
                    if (isFavorite) {
                        bookmarkViewModel.insertToFavorite(data, isFavorite)
                    } else {
                        bookmarkViewModel.deleteFromFavorite(data, isFavorite)
                    }
                }
            }
        }
    }

    private fun setIconButton(isFavorite: Boolean) {
        binding.icFavorite.setImageResource(if (isFavorite) R.drawable.ic_love else R.drawable.ic_love_grey)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            binding.contentDetail.visibility = View.GONE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            binding.contentDetail.visibility = View.VISIBLE
        }
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
        adoptViewModel.getDetailPet(token, petId!!)
    }
}