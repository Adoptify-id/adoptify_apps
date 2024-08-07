package com.example.adoptify_core.ui.adopt.list

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ListPageAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListPetFragment.newInstance("Kucing")
            1 -> ListPetFragment.newInstance("Anjing")
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}