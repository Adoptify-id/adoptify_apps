package com.example.adoptify_core.ui.adopt.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ListPageAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = ListPetFragment()

        fragment.arguments = Bundle().apply {
            putInt(ListPetFragment.ARG_POSITION, position + 1)
        }

        return fragment
    }
}