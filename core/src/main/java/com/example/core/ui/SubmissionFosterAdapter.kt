package com.example.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.R
import com.example.core.databinding.ListSubmissionFosterBinding
import com.example.core.domain.model.DataSubmissionFoster

class SubmissionFosterAdapter(
    private val submissionFoster: List<DataSubmissionFoster>,
    private val onItemClick: (DataSubmissionFoster) -> Unit
) : RecyclerView.Adapter<SubmissionFosterAdapter.SubmissionFosterHolder>() {
    inner class SubmissionFosterHolder(private val binding: ListSubmissionFosterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataSubmissionFoster) {
            binding.apply {
                txtName.text = data.name
                adoptPet.text = if (data.kategori == "Kucing") "Adopsi Kucing Ras ${data.ras} - ${data.namePet}" else "Adopsi Anjing Ras ${data.ras} - ${data.namePet}"
                Glide.with(itemView.context)
                    .load("https://storage.googleapis.com/bucket-adoptify/imagesUser/${data.foto}")
                    .placeholder(R.drawable.adopt_virtual_dog)
                    .into(imgUser)
                root.setOnClickListener { onItemClick(data) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionFosterHolder {
        val binding = ListSubmissionFosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubmissionFosterHolder(binding)
    }

    override fun getItemCount(): Int = submissionFoster.size

    override fun onBindViewHolder(holder: SubmissionFosterHolder, position: Int) {
       holder.bind(submissionFoster[position])
    }
}