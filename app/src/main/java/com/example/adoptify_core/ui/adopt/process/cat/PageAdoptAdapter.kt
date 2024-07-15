package com.example.adoptify_core.ui.adopt.process.cat

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adoptify_core.ui.adopt.process.cat.AgreementFragment
import com.example.adoptify_core.ui.adopt.process.cat.PersonalDataFragment
import com.example.adoptify_core.ui.adopt.process.cat.SecondSubmissionFragment
import com.example.adoptify_core.ui.adopt.process.cat.SubmissionFragment
import com.example.adoptify_core.ui.adopt.process.cat.ThirdSubmissionFragment

class PageAdoptAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when(position) {
            0 -> fragment = AgreementFragment()
            1 -> fragment = PersonalDataFragment()
            2 -> fragment = SubmissionFragment()
            3 -> fragment = SecondSubmissionFragment()
            4 -> fragment = ThirdSubmissionFragment()
        }

        return fragment as Fragment
    }
}