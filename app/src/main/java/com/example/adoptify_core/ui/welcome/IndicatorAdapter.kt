package com.example.adoptify_core.ui.welcome

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.adoptify_core.ui.welcome.first.FirstWelcomeFragment
import com.example.adoptify_core.ui.welcome.fourth.FourthWelcomeFragment
import com.example.adoptify_core.ui.welcome.second.SecondWelcomeFragment
import com.example.adoptify_core.ui.welcome.third.ThirdWelcomeFragment

class IndicatorAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position) {
            0 -> fragment = FirstWelcomeFragment()
            1 -> fragment = SecondWelcomeFragment()
            2 -> fragment = ThirdWelcomeFragment()
            3 -> fragment = FourthWelcomeFragment()
        }

        return fragment as Fragment
    }

}