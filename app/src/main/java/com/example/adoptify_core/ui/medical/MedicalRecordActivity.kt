package com.example.adoptify_core.ui.medical


import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityMedicalRecordBinding
import com.example.adoptify_core.ui.medical.record.RecordActivity
import com.example.adoptify_core.ui.medical.vaksinasi.VaksinasiActivity
import com.example.core.data.Resource
import com.example.core.domain.model.ListMedicalItem
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.VaksinasiData
import com.example.core.ui.MedicalRecordAdapter
import com.example.core.utils.SessionViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.Q)
class MedicalRecordActivity : BaseActivity() {

    private lateinit var binding: ActivityMedicalRecordBinding

    private val medicalViewModel: MedicalRecordViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var token: String? = null
    private var userId: Int? = null

    private var dataVaksinasi: List<VaksinasiData> = listOf()
    private var dataMedical: List<MedicalItem> = listOf()

    private lateinit var medicalRecordAdapter: MedicalRecordAdapter

    private lateinit var vaksinasiActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var medicalActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val combinedData = mutableListOf<ListMedicalItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        vaksinasiActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                observeData()
            }
        }

        medicalActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                observeData()
            }
        }

        observeData()
        setupObserver()
        setupListener()
    }

    private fun observeData() {
        sessionViewModel.token.observe(this) {
            token = it
            if (token != null && userId != null) {
                getVaksinasi()
                getMedicalRecord()
            }
        }

        sessionViewModel.userId.observe(this) {
            userId = it
            if (token != null && userId != null) {
                getVaksinasi()
                getMedicalRecord()
            }
        }
    }

    private fun setupObserver() {
        medicalViewModel.vaksinasi.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    dataVaksinasi = it.data ?: listOf()
                    updateCombinedData()
                    binding.swipeRefresh.isRefreshing = false
                }

                is Resource.Error -> {
                    showLoading(false)
                    checkContent()
                    binding.swipeRefresh.isRefreshing = false
                    Log.d("Vaksinasi", "error: ${it.message}")
                }
            }
        }

        medicalViewModel.medical.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    dataMedical = it.data ?: listOf()

                    updateCombinedData()
                    binding.swipeRefresh.isRefreshing = false
                }

                is Resource.Error -> {
                    showLoading(false)
                    checkContent()
                    binding.swipeRefresh.isRefreshing = false
                    Log.d("MedicalRecord", "error: ${it.message}")
                }
            }
        }
    }

    private fun getVaksinasi() {
        medicalViewModel.getVaksinasi(token!!, userId!!)
    }

    private fun getMedicalRecord() {
        medicalViewModel.getMedicalRecord(token!!, userId!!)
    }

    private fun updateCombinedData() {
        combinedData.clear()
        combinedData.addAll(dataVaksinasi.map { data -> ListMedicalItem.VaksinasiItem(data) })
        combinedData.addAll(dataMedical.map { data -> ListMedicalItem.MedicalData(data) })
        showRecylerView()
        checkContent()
    }

    private fun sortItemMedicalRecord(): List<ListMedicalItem> {
        val groupedData = mutableListOf<ListMedicalItem>()
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fullDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))

        val sortedData = combinedData.sortedByDescending {
            when (it) {
                is ListMedicalItem.VaksinasiItem -> parseDateToLong(it.data.created_at)
                is ListMedicalItem.MedicalData -> parseDateToLong(it.data.createdAt)
                else -> 0L
            }
        }
        var lastDate = ""

        sortedData.forEach { item ->
            val itemDateStr = when (item) {
                is ListMedicalItem.VaksinasiItem -> item.data.created_at!!.split("T")[0]
                is ListMedicalItem.MedicalData -> item.data.createdAt!!.split("T")[0]
                else -> null
            }

            val itemDate = dateFormat.parse(itemDateStr)
            val header = when {
                dateFormat.format(today.time) == itemDateStr -> "Hari ini"
                dateFormat.format(yesterday.time) == itemDateStr -> "Kemarin"
                else -> fullDateFormat.format(itemDate)
            }

            if (header != lastDate) {
                groupedData.add(ListMedicalItem.HeaderItem(header))
                lastDate = header
            }

            groupedData.add(item)
        }
        return groupedData
    }

    private fun parseDateToLong(dateString: String?): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun setupListener() {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        binding.apply {
            cardMedical.btnMedical.setOnClickListener {
                val intent = Intent(this@MedicalRecordActivity, RecordActivity::class.java)
                medicalActivityLauncher.launch(intent, options)

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "menu_medical_checkup_record")
                firebaseAnalytics.logEvent("navigate_to_medical_checkup_record", bundle)

            }
            cardMedical.btnVaksinasi.setOnClickListener {
                val intent = Intent(this@MedicalRecordActivity, VaksinasiActivity::class.java)
                vaksinasiActivityLauncher.launch(intent, options)

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "menu_vaccination_record")
                firebaseAnalytics.logEvent("navigate_to_vaccination_record", bundle)
            }

            icArrowBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            swipeRefresh.setOnRefreshListener {
                observeData()
                getVaksinasi()
                getMedicalRecord()
            }
        }
    }

    private fun checkContent() {
        val isDataEmpty = dataVaksinasi.isEmpty() && dataMedical.isEmpty()
        showContent(isDataEmpty)
    }

    private fun showRecylerView() {
        val groupedData = sortItemMedicalRecord()
        medicalRecordAdapter = MedicalRecordAdapter(groupedData)
        binding.rvActivity.apply {
            layoutManager = LinearLayoutManager(this@MedicalRecordActivity)
            setHasFixedSize(true)
            adapter = medicalRecordAdapter
        }
    }

    private fun showContent(isShowing: Boolean) {
        binding.contentNull.apply {
            layout.visibility = if (isShowing) View.VISIBLE else View.GONE
            btnClose.visibility = View.GONE
            txtDesc.text =
                "Maaf, data medical record Anda tidak tersedia. Coba muat ulang atau periksa kembali nanti."
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        getVaksinasi()
        getMedicalRecord()
    }
}