package com.example.adoptify_core.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentHomeBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.coming.ComingSoonActivity
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.medical.MedicalRecordActivity
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.adoptify_core.ui.virtual.main.VirtualPetActivity
import com.example.core.data.Resource
import com.example.core.domain.model.VirtualPetItem
import com.example.core.ui.VirtualPetAdapter
import com.example.core.utils.SessionManager
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates


@RequiresApi(Build.VERSION_CODES.Q)
class HomeFragment : Fragment() {

    private var _homeFragment: FragmentHomeBinding? = null
    private val homeFragment get() = _homeFragment!!

    private val mainViewModel: MainViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val sessionManager: SessionManager by inject()
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

        Log.d("MainActivity", "SessionManager initialized: $sessionManager")

        homeFragment.swipeRefresh.apply {
            setOnRefreshListener {
                getToken()
                getUserId()
            }
            setColorSchemeColors(resources.getColor(R.color.primaryColor))
        }
    }

    private fun initData() {
        mainViewModel.getName()
        loginViewModel.getSession()
        mainViewModel.getUserId()
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    token = it.data
                    isTokenAvailable = true
                    homeFragment.swipeRefresh.isRefreshing = false
                    if (isTokenAvailable && isUserIdAvailable) {
                        getVirtualPet()
                        getProfileUser()
                    }
                    Log.d("HomeFragment", "check: $token")
                }

                is Resource.Error -> {}
            }
        }
    }

    private fun getName() {
        mainViewModel.name.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    val name = it.data.split(" ").joinToString(separator = " ") { it.capitalize() }
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
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    userId = it.data
                    isUserIdAvailable = true
                    homeFragment.swipeRefresh.isRefreshing = false
                    if (isTokenAvailable && isUserIdAvailable) {
                        getVirtualPet()
                        getProfileUser()
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
                    showContent(true)
                    Log.d("HomeFragment", "loading: true")
                }

                is Resource.Success -> {
                    showLoading(false)
                    Log.d("HomeFragment", "data: ${it.data}")
                    virtualPet = it.data
                    homeFragment.swipeRefresh.isRefreshing = false
                    if (virtualPet.isNotEmpty()) {
                        showContent(false)
                        showVirtualPet(virtualPet)
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    showContent(true)
                    Log.d("HomeFragment", "error: ${it.message}")
                }
            }
        }
    }

    private fun getProfileUser() {
        profileViewModel.getDetailUser(token, userId)
        profileViewModel.detail.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    it.data.data?.map { data ->
                        val imageUrl =
                            "https://storage.googleapis.com/bucket-adoptify/imagesUser/${data?.foto}"
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.dummy_profile)
                            .into(homeFragment.imgProfile)

                        if (data?.alamat.isNullOrEmpty() || data?.provinsi.isNullOrEmpty()) {
                            homeFragment.txtLocation.text = "Location not set"
                        } else {
                            homeFragment.txtLocation.text = "${data?.alamat}, ${data?.provinsi}"
                        }
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun showVirtualPet(data: List<VirtualPetItem>) {
        homeFragment.apply {
            val indicator = dotsIndicator
            val viewPager: ViewPager2 = virtualPetPager
            val adapter = VirtualPetAdapter(data.take(3))
            viewPager.isUserInputEnabled = false
            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    btnBack.visibility = if (position == 0) View.GONE else View.VISIBLE
                }
            })
            indicator.attachTo(viewPager)
        }
    }


    private fun setupListener() {
        homeFragment.apply {
            btnNext.setOnClickListener { virtualPetPager.currentItem += 1 }
            btnBack.setOnClickListener { virtualPetPager.currentItem -= 1 }
            btnMedicalRecord.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        MedicalRecordActivity::class.java
                    )
                )
            }
            btnVirtualPet.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        VirtualPetActivity::class.java
                    )
                )
            }
            btnMoreRewards.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        ComingSoonActivity::class.java
                    )
                )
            }
            cardCat.root.setOnClickListener { }
            cardDog.root.setOnClickListener { }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        homeFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showContent(isShowing: Boolean) {
        homeFragment.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE

        homeFragment.contentNull.btnClose.setOnClickListener {
            val intent = Intent(requireContext(), VirtualPetActivity::class.java)
            intent.putExtra("token", token)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _homeFragment = null
    }

    override fun onPause() {
        super.onPause()
    }
}