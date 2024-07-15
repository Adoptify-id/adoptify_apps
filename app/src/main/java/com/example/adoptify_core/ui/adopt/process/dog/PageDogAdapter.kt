package com.example.adoptify_core.ui.adopt.process.dog

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageDogAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when(position) {
            0 -> fragment = AgreementDogFragment()
            1 -> fragment = PersonalDataDogFragment()
            2 -> fragment = SubmissionDogFragment()
            3 -> fragment = SecondSubmissionDogFragment()
            4 -> fragment = ThirdSubmissionDogFragment()
        }

        return fragment as Fragment
    }
}