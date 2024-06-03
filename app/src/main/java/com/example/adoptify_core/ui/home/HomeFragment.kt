package com.example.adoptify_core.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.databinding.FragmentHomeBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.medical.MedicalRecordActivity
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.adoptify_core.ui.virtual.main.VirtualPetActivity
import com.example.core.data.Resource
import com.example.core.domain.model.VirtualPetItem
import com.example.core.ui.VirtualPetAdapter
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates


@RequiresApi(Build.VERSION_CODES.Q)
class HomeFragment : Fragment() {

    private var _homeFragment: FragmentHomeBinding? = null
    private val homeFragment get() = _homeFragment!!

    private val mainViewModel: MainViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private val virtualPetViewModel: VirtualPetViewModel by viewModel()

    private var token = ""
    private var virtualPet: List<VirtualPetItem> = listOf()
    private var userId by Delegates.notNull<Int>()

    private var isTokenAvailable = false
    private var isUserIdAvailable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _homeFragment = FragmentHomeBinding.inflate(inflater, container, false)
        return _homeFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        getToken()
        getUserId()
        getName()
        setupListener()

    }

    private fun initData() {
        mainViewModel.getName()
        loginViewModel.getSession()
        mainViewModel.getUserId()
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    token = it.data
                    isTokenAvailable = true
                    if (isTokenAvailable && isUserIdAvailable) {
                        getVirtualPet()
                    }
                    Log.d("HomeFragment", "check: $token")
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun getName() {
        mainViewModel.name.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    val name = it.data.split(" ").joinToString { it.capitalize() }
                    homeFragment.txtUsername.text = "Halo, ${name}!"
                    Log.d("Home", "username: $name")
                }
                is Resource.Error -> {
                    Log.d("Home", "error: ${it.message}")
                }
            }
        }
    }

    private fun getUserId() {
        mainViewModel.userId.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    userId = it.data
                    isUserIdAvailable = true
                    if (isTokenAvailable && isUserIdAvailable) {
                        getVirtualPet()
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("Home", "error: ${it.message}")
                }
            }
        }
    }

    private fun getVirtualPet() {
        virtualPetViewModel.getVirtualPet(token, userId)
        virtualPetViewModel.virtualPet.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                    Log.d("HomeFragment", "loading: true")
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("HomeFragment", "data: ${it.data}")
                    virtualPet = it.data
                    homeFragment.apply {
                        val indicator = dotsIndicator
                        val viewPager: ViewPager2 = virtualPetPager
                        val adapter = VirtualPetAdapter(it.data)
                        viewPager.isUserInputEnabled = false
                        viewPager.adapter = adapter
                        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                                btnBack.visibility = if (position == 0) View.GONE else View.VISIBLE
                            }
                        })
                        indicator.attachTo(viewPager)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("HomeFragment", "error: ${it.message}")
                }
            }
        }
    }


    private fun setupListener() {
        homeFragment.apply {
            btnNext.setOnClickListener { virtualPetPager.currentItem += 1 }
            btnBack.setOnClickListener { virtualPetPager.currentItem -= 1 }
            btnMedicalRecord.setOnClickListener {
                val intent = Intent(requireContext(), MedicalRecordActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }
            btnVirtualPet.setOnClickListener {
                val intent = Intent(requireContext(), VirtualPetActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        homeFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _homeFragment = null
    }

}