package com.example.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.R
import com.example.core.databinding.ListCardAdoptBinding
import com.example.core.domain.model.DataAdopt

class PetItemAdapter(
    private val petList: List<DataAdopt>,
    private val onItemClick: (DataAdopt) -> Unit
) : RecyclerView.Adapter<PetItemAdapter.PetItemHolder>() {
    inner class PetItemHolder(private val binding: ListCardAdoptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataAdopt) {
            binding.apply {
                namePet.text = data.namePet.split(" ").joinToString(separator = " ") { it.capitalize() }
                genderPet.text = data.gender
                agePet.text = "${data.umur} Bulan"
                rasPet.text = data.ras
                Glide.with(itemView.context)
                    .load("https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}")
                    .placeholder(R.drawable.adopt_virtual_dog)
                    .into(imgPet)

                root.setOnClickListener { onItemClick(data) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetItemHolder {
        val binding =
            ListCardAdoptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetItemHolder(binding)
    }

    override fun getItemCount(): Int = petList.size

    override fun onBindViewHolder(holder: PetItemHolder, position: Int) {
        holder.bind(petList[position])
    }
}