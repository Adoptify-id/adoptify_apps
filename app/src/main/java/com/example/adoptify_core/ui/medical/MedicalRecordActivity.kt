package com.example.adoptify_core.ui.medical


import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.databinding.ActivityMedicalRecordBinding
import com.example.adoptify_core.ui.medical.record.RecordActivity
import com.example.adoptify_core.ui.medical.vaksinasi.VaksinasiActivity
import com.example.core.data.Resource
import com.example.core.domain.model.ListMedicalItem
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.VaksinasiData
import com.example.core.ui.MedicalRecordAdapter
import com.example.core.utils.formatDateString
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.Q)
class MedicalRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicalRecordBinding

    private val medicalViewModel: MedicalRecordViewModel by viewModel()

    private lateinit var token: String
    private var userId by Delegates.notNull<Int>()

    private var dataVaksinasi: List<VaksinasiData> = listOf()
    private var dataMedical: List<MedicalItem> = listOf()

    private lateinit var medicalRecordAdapter: MedicalRecordAdapter

    private lateinit var vaksinasiActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var medicalActivityLauncher: ActivityResultLauncher<Intent>

    private val combinedData = mutableListOf<ListMedicalItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vaksinasiActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                initData()
                getVaksinasi()
            }
        }

        medicalActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                initData()
                getMedicalRecord()
            }
        }

        initData()
        getVaksinasi()
        getMedicalRecord()
        setupListener()
    }

    private fun initData() {
        token = intent?.getStringExtra("token") ?: ""
        userId = intent.getIntExtra("userId",0)
        Log.d("Vaksinasi", "initData: $token and $userId")
        medicalViewModel.getVaksinasi(token, userId)
        medicalViewModel.getMedicalRecord(token, userId)
    }

    private fun getVaksinasi() {
        medicalViewModel.vaksinasi.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    dataVaksinasi = it.data
                    combinedData.addAll(dataVaksinasi.map { data -> ListMedicalItem.VaksinasiItem(data) })
                    updateCombinedData()
                    Log.d("Vaksinasi", "data: $dataVaksinasi")
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("Vaksinasi", "error: ${it.message}")
                }
            }
        }
    }

    private fun getMedicalRecord() {
        medicalViewModel.medical.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    dataMedical = it.data
                    combinedData.addAll(dataMedical.map { data -> ListMedicalItem.MedicalData(data) })
                    updateCombinedData()
                    Log.d("MedicalRecord", "data: ${it.data}")

                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("MedicalRecord", "error: ${it.message}")
                }
            }
        }
    }

    private fun updateCombinedData() {
        combinedData.clear()
        combinedData.addAll(dataVaksinasi.map { data -> ListMedicalItem.VaksinasiItem(data) })
        combinedData.addAll(dataMedical.map { data -> ListMedicalItem.MedicalData(data) })
        showRecylerView()
    }

    private fun sortItemMedicalRecord(): List<ListMedicalItem> {
        val groupedData = mutableListOf<ListMedicalItem>()
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fullDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id","ID"))

        val sortedData = combinedData.sortedByDescending {
            when(it) {
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
        binding.apply {
            cardMedical.btnMedical.setOnClickListener {
                val intent = Intent(this@MedicalRecordActivity, RecordActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("userId", userId)
                medicalActivityLauncher.launch(intent)
            }
            cardMedical.btnVaksinasi.setOnClickListener {
                val intent = Intent(this@MedicalRecordActivity, VaksinasiActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("userId", userId)
                vaksinasiActivityLauncher.launch(intent)
            }
        }
    }

    private fun showRecylerView() {
        val groupedData = sortItemMedicalRecord()
        medicalRecordAdapter = MedicalRecordAdapter(groupedData)
        binding.rvActivity.apply {
            layoutManager = LinearLayoutManager(this@MedicalRecordActivity)
            setHasFixedSize(true)
            adapter = this@MedicalRecordActivity.medicalRecordAdapter
            medicalRecordAdapter.notifyDataSetChanged()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}