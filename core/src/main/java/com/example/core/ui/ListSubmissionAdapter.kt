package com.example.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.R
import com.example.core.databinding.ListSubmissionAdoptBinding
import com.example.core.domain.model.SubmissionItem

class ListSubmissionAdapter(
    private val submissionList: List<SubmissionItem>,
    private val onItemClick: (SubmissionItem) -> Unit
) : RecyclerView.Adapter<ListSubmissionAdapter.ListSubmissionHolder>() {
    inner class ListSubmissionHolder(private val binding: ListSubmissionAdoptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SubmissionItem) {
            binding.apply {
                adoptPet.text = if (data.kategori == "Kucing") "Adopsi Kucing" else "Adopsi Anjing"
                fosterName.text = data.fullName
                rasPet.text = "Ras ${data.ras} - ${data.namePet}"
                Glide.with(itemView.context)
                    .load("https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}")
                    .placeholder(R.drawable.adopt_virtual_dog)
                    .into(imgPet)

                root.setOnClickListener { onItemClick(data) }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSubmissionHolder {
        val binding =
            ListSubmissionAdoptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListSubmissionHolder(binding)
    }

    override fun getItemCount(): Int = submissionList.size

    override fun onBindViewHolder(holder: ListSubmissionHolder, position: Int) {
        holder.bind(submissionList[position])
    }
}