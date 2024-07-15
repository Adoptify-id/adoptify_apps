package com.example.adoptify_core.ui.foster.form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adoptify_core.ui.foster.form.care.ReviewCareFragment
import com.example.adoptify_core.ui.foster.form.general.ReviewGeneralFragment
import com.example.adoptify_core.ui.foster.form.personal.ReviewPersonalFragment

class ReviewFormAdapter(activity: AppCompatActivity, private val reqId: Int, private val category: String) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
       var fragment: Fragment? = null

        when(position) {
            0 -> fragment = ReviewPersonalFragment()
            1 -> fragment = ReviewGeneralFragment()
            2 -> fragment = ReviewCareFragment()
        }
        fragment?.arguments = Bundle().apply {
            putInt("REQ_ID", reqId)
            putString("CATEGORY", category)
        }

        return fragment as Fragment
    }

}