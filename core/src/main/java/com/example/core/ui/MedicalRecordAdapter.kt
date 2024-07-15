package com.example.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.R
import com.example.core.databinding.ListCardMedicalRecordBinding
import com.example.core.databinding.ListHeaderBinding
import com.example.core.domain.model.ListMedicalItem
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.VaksinasiData
import com.example.core.utils.formatDateString

class MedicalRecordAdapter(private val items: List<ListMedicalItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_VAKSINASI = 1
        private const val TYPE_MEDICAL = 2
    }


    class HeaderViewHolder(private val binding: ListHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.headerText.text = header
        }
    }

    class VaksinItemHolder(private val binding: ListCardMedicalRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: VaksinasiData) {
            binding.apply {
                titleActivity.text = "Vaksin ${data.jenisVaksin.split(" ").joinToString(separator = " ") { it.capitalize() }}"
                historyCatVisual.setImageResource(if (data.kategoriPet == "Kucing") R.drawable.history_cat else R.drawable.history_dog)
                timeActivity.text = formatDateString(data.created_at.toString())
                clinicName.text = data.klinikName.split(" ").joinToString(separator = " ") { it.capitalize() }
                petName.text = data.name.split(" ").joinToString(separator = " ") { it.capitalize() }
                petWeight.text = "${data.beratPet} Kg"
                petCondition.text = data.kesehatan
                petClinic.text = data.klinikName
                addressClinic.text = data.alamat
                doctorClinic.text = data.dokterName.split(" ").joinToString(separator = " ") { it.capitalize() }
                notesClinic.text = data.catatan

                layout.setOnClickListener { expand() }
            }
        }

        private fun expand() {
            val isVisible = binding.detailData.visibility == View.VISIBLE
            val newVisible = if (isVisible) View.GONE else View.VISIBLE
//            TransitionManager.beginDelayedTransition(binding.layout, AutoTransition())
            binding.detailData.visibility = newVisible
        }
    }

    class MedicalRecordItemHolder(private val binding: ListCardMedicalRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MedicalItem) {
           binding.apply {
               titleActivity.text = "Medical Record"
               historyCatVisual.setImageResource(if (data.kategoriPet == "Kucing") R.drawable.history_cat else R.drawable.history_dog)
               timeActivity.text = formatDateString(data.createdAt.toString())
               clinicName.text = data.klinikName?.split(" ")?.joinToString(separator = " ") { it.capitalize() }
               petName.text = data.namePet?.split(" ")?.joinToString(separator = " ") { it.capitalize() }
               petWeight.text = "${data.beratPet} Kg"
               petCondition.text = data.kesehatan
               petClinic.text = data.klinikName
               addressClinic.text = data.alamat
               doctorClinic.text = data.dokterName?.split(" ")?.joinToString(separator = " ") { it.capitalize() }
               notesClinic.text = data.catatan

               val imageUrl = if (data.xRay == null) {
                  null
               } else {
                   "https://storage.googleapis.com/bucket-adoptify/imagesMedical/" + data.xRay
               }

               Glide.with(itemView.context)
                   .load(imageUrl ?: R.drawable.ic_preview_image)
                   .placeholder(R.drawable.ic_preview_image)
                   .into(imageXray)

//               layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

               layout.setOnClickListener { expand() }
           }
        }

        private fun expand() {
            val isVisible = binding.detailData.visibility == View.VISIBLE
            val newVisible = if (isVisible) View.GONE else View.VISIBLE
//            TransitionManager.beginDelayedTransition(binding.layout, AutoTransition())
            binding.detailData.visibility = newVisible
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is ListMedicalItem.HeaderItem -> TYPE_HEADER
            is ListMedicalItem.VaksinasiItem -> TYPE_VAKSINASI
            is ListMedicalItem.MedicalData -> TYPE_MEDICAL
            else -> { throw IllegalArgumentException("Error") }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListCardMedicalRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when(viewType) {
            TYPE_HEADER -> {
                val binding = ListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_VAKSINASI -> VaksinItemHolder(binding)
            TYPE_MEDICAL -> MedicalRecordItemHolder(binding)
            else -> throw IllegalArgumentException("Invalid view")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]) {
            is ListMedicalItem.HeaderItem -> (holder as HeaderViewHolder).bind(item.header)
            is ListMedicalItem.VaksinasiItem -> (holder as VaksinItemHolder).bind(item.data)
            is ListMedicalItem.MedicalData -> (holder as MedicalRecordItemHolder).bind(item.data)
            else -> {}
        }
    }
}