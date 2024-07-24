package com.example.adoptify_core.ui.adopt.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adoptify_core.ui.adopt.review.care.ReviewCareUserFragment
import com.example.adoptify_core.ui.adopt.review.general.ReviewGeneralUserFragment
import com.example.adoptify_core.ui.adopt.review.personal.ReviewPersonalUserFragment

class ReviewFormUserAdapter(fragment: AppCompatActivity, private val reqId: Int, private val category: String) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when(position) {
            0 -> fragment = ReviewPersonalUserFragment()
            1 -> fragment = ReviewGeneralUserFragment()
            2 -> fragment = ReviewCareUserFragment()
        }

        fragment?.arguments = Bundle().apply {
            putInt("REQ_ID", reqId)
            putString("CATEGORY", category)
        }

        return fragment as Fragment
    }
}