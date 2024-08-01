package com.example.adoptify_core.ui.main

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.adoptify_core.BaseActivity
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment)
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.bookmarkFragment -> {
                    if (navController.currentDestination?.id != R.id.bookmarkFragment) {
                        navController.navigate(R.id.bookmarkFragment)
                    }
                    true
                }
                R.id.adoptFragment -> {
                    if (navController.currentDestination?.id != R.id.adoptFragment) {
                        navController.navigate(R.id.adoptFragment)
                    }
                    true
                }
                R.id.profileFragment -> {
                    if (navController.currentDestination?.id != R.id.profileFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun navigateToAdopt() {
        val navController = findNavController(R.id.fragment)
        navController.navigate(R.id.adoptFragment)
        binding.bottomNavigation.selectedItemId = R.id.adoptFragment
    }
}