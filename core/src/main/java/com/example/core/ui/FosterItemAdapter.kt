package com.example.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.R
import com.example.core.databinding.ListCardAdoptBinding
import com.example.core.databinding.ListHeaderBinding
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.ListPetItem

class FosterItemAdapter(private val items: List<ListPetItem>, private val onItemClick: (DataAdopt, ImageView) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PET = 1
    }

    inner class HeaderViewHolder(private val binding: ListHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.headerText.text = header
            val layoutParams = binding.headerText.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = 50
            binding.headerText.layoutParams = layoutParams
        }
    }

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

                imgPet.transitionName = "shared_image_${data.petId}"

                root.setOnClickListener { onItemClick(data, imgPet) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is ListPetItem.HeaderItem -> TYPE_HEADER
            is ListPetItem.PetItem -> TYPE_PET
            else -> { throw IllegalArgumentException("Error") }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListCardAdoptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when(viewType) {
            TYPE_HEADER -> {
                val binding = ListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
           TYPE_PET -> PetItemHolder(binding)
            else -> throw IllegalArgumentException("Invalid view")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]) {
            is ListPetItem.HeaderItem -> (holder as HeaderViewHolder).bind(item.header)
            is ListPetItem.PetItem -> (holder as FosterItemAdapter.PetItemHolder).bind(item.data)
        }
    }
}
