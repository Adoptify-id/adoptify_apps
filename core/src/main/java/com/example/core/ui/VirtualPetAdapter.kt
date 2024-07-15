package com.example.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core.R
import com.example.core.databinding.CardTypePetBinding
import com.example.core.databinding.CardVirtualPetBinding
import com.example.core.domain.model.VirtualPetItem

class VirtualPetAdapter(private val virtualPetList: List<VirtualPetItem>) :
    RecyclerView.Adapter<VirtualPetAdapter.VirtualPetItemHolder>() {
    class VirtualPetItemHolder(private val binding: CardVirtualPetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: VirtualPetItem) {
            binding.apply {
                pet.text = data.category
                categoryDisplay.setImageResource(if (data.category == "Kucing") R.drawable.virtual_cat else R.drawable.virtual_dog)
                namePet.text = data.name_pet.split(" ").joinToString { it.uppercase() }
                agePet.text = "${data.umur} Bulan"
                weightPet.text = "${data.berat_pet} Kg"
                rasPet.text = data.ras_pet
                genderPet.text = data.gender
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VirtualPetItemHolder {
        val binding =
            CardVirtualPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VirtualPetItemHolder(binding)
    }

    override fun getItemCount(): Int = virtualPetList.size

    override fun onBindViewHolder(holder: VirtualPetItemHolder, position: Int) {
        holder.bind(virtualPetList[position])
    }
}