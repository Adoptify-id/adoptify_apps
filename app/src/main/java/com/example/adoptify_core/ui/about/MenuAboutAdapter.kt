package com.example.adoptify_core.ui.about

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adoptify_core.ui.about.app.AppFragment
import com.example.adoptify_core.ui.about.developer.DeveloperFragment

class MenuAboutAdapter(fragment: AppCompatActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AppFragment()
            1 -> DeveloperFragment()
            else -> throw IllegalArgumentException("Layout not found")
        }
    }
}