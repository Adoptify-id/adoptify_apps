package com.example.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.R
import com.example.core.data.source.local.entity.PetEntity
import com.example.core.databinding.ListCardAdoptBinding

class FavoriteItemAdapter(
    private val petList: List<PetEntity>,
    private val onItemClick: (PetEntity, ImageView) -> Unit
) : RecyclerView.Adapter<FavoriteItemAdapter.FavoriteItemHolder>() {
    inner class FavoriteItemHolder(private val binding: ListCardAdoptBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PetEntity) {
            binding.apply {
                namePet.text = data.namePet.split(" ").joinToString(separator = " ") { it.capitalize() }
                genderPet.text = data.gender
                agePet.text = "${data.age} Bulan"
                rasPet.text = data.ras
                Glide.with(itemView.context)
                    .load("https://storage.googleapis.com/bucket-adoptify/imagesPet/${data.fotoPet}")
                    .placeholder(R.drawable.adopt_virtual_dog)
                    .into(imgPet)

                imgPet.transitionName = "shared_image_${data.id}"

                root.setOnClickListener { onItemClick(data, imgPet) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemHolder {
        val binding = ListCardAdoptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteItemHolder(binding)
    }

    override fun getItemCount(): Int = petList.size

    override fun onBindViewHolder(holder: FavoriteItemHolder, position: Int) {
       holder.bind(petList[position])
    }
}