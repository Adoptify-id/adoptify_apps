package com.example.adoptify_core.ui.foster

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adoptify_core.ui.foster.add.AddPetFragment
import com.example.adoptify_core.ui.foster.dashboard.DashboardFragment

class FosterMenuAdapter(fragment: AppCompatActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
       return when(position) {
           0 -> DashboardFragment()
           1 -> AddPetFragment()
           else -> throw IllegalArgumentException("Layout not found")
       }
    }
}