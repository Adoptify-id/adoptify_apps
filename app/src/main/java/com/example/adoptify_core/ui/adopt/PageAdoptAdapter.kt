package com.example.adoptify_core.ui.adopt

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adoptify_core.ui.adopt.process.AgreementFragment
import com.example.adoptify_core.ui.adopt.process.PersonalDataFragment
import com.example.adoptify_core.ui.adopt.process.StatusAdoptFragment
import com.example.adoptify_core.ui.adopt.process.SubmissionFragment

class PageAdoptAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when(position) {
            0 -> fragment = AgreementFragment()
            1 -> fragment = PersonalDataFragment()
            2 -> fragment = SubmissionFragment()
            3 -> fragment = StatusAdoptFragment()
        }

        return fragment as Fragment
    }
}