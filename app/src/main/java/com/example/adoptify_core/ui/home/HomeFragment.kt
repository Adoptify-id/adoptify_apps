package com.example.adoptify_core.ui.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentHomeBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.coming.ComingSoonActivity
import com.example.adoptify_core.ui.main.MainActivity
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.medical.MedicalRecordActivity
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.adoptify_core.ui.virtual.VirtualPetViewModel
import com.example.adoptify_core.ui.virtual.main.VirtualPetActivity
import com.example.core.data.Resource
import com.example.core.domain.model.VirtualPetItem
import com.example.core.ui.VirtualPetAdapter
import com.example.core.utils.SessionManager
import com.google.firebase.analytics.FirebaseAnalytics
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

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var token = ""
    private var virtualPet: List<VirtualPetItem> = listOf()
    private var userId by Delegates.notNull<Int>()

    private var isTokenAvailable = false
    private var isUserIdAvailable = false

    private var isErrorDialogShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _homeFragment = FragmentHomeBinding.inflate(inflater, container, false)
        return _homeFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        initData()
        getToken()
        getUserId()
        getName()
        setupListener()

        Log.d("MainActivity", "SessionManager initialized: $sessionManager")

        homeFragment.swipeRefresh.apply {
            setOnRefreshListener {
                showLoading(true)
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
                    if (isTokenAvailable && isUserIdAvailable) {
                        getVirtualPet()
                        getProfileUser()
                    }
                    Log.d("HomeFragment", "check: $token")
                }

                is Resource.Error -> {}
            }
            homeFragment.swipeRefresh.isRefreshing = false
        }
    }

    private fun getName() {
        mainViewModel.name.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> { showLoading(true) }
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
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    userId = it.data
                    isUserIdAvailable = true
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
            homeFragment.swipeRefresh.isRefreshing = false
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
                    if (it.message.contains("Tidak ada koneksi internet.", ignoreCase = true)) {
                        showError(it.message)
                    }
                    Log.d("HomeFragment", "error: ${it.message}")
                }
            }
        }
    }

    private fun getProfileUser() {
        profileViewModel.getDetailUser(token, userId)
        profileViewModel.detail.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> { showLoading(true) }
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
                            homeFragment.txtLocation.text = "Lokasi tidak disetel"
                        } else {
                            homeFragment.txtLocation.text = "${data?.alamat}, ${data?.provinsi}"
                        }
                    }
                }
                is Resource.Error -> { showLoading(false) }
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
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        homeFragment.apply {
            btnNext.setOnClickListener { virtualPetPager.currentItem += 1 }
            btnBack.setOnClickListener { virtualPetPager.currentItem -= 1 }
            btnMedicalRecord.setOnClickListener {
                startActivity(Intent(requireContext(), MedicalRecordActivity::class.java), options.toBundle())
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_medical_record")
                firebaseAnalytics.logEvent("navigate_to_medical_record", bundle)
            }
            btnVirtualPet.setOnClickListener {
                startActivity(Intent(requireContext(), VirtualPetActivity::class.java), options.toBundle())
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_virtual_pet")
                firebaseAnalytics.logEvent("navigate_to_virtual_pet", bundle)
            }
            btnMoreRewards.setOnClickListener {
                startActivity(Intent(requireContext(), ComingSoonActivity::class.java), options.toBundle())
            }
            cardCat.root.setOnClickListener {
                (activity as? MainActivity)?.navigateToAdopt()

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "card_cat_adopt")
                firebaseAnalytics.logEvent("navigate_to_adopt_fragment", bundle)
            }
            cardDog.root.setOnClickListener {
                (activity as? MainActivity)?.navigateToAdopt()

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "card_dog_adopt")
                firebaseAnalytics.logEvent("navigate_to_adopt_fragment", bundle)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading) {
            homeFragment.shimmerLayout?.startShimmer()
            homeFragment.shimmerLayout?.visibility = View.VISIBLE
            homeFragment.contentLayout?.visibility = View.GONE
        } else {
            homeFragment.shimmerLayout?.stopShimmer()
            homeFragment.shimmerLayout?.visibility = View.GONE
            homeFragment.contentLayout?.visibility = View.VISIBLE
        }
    }

    private fun showContent(isShowing: Boolean) {
        homeFragment.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE
        homeFragment.contentNull.btnClose.setOnClickListener {
            val intent = Intent(requireContext(), VirtualPetActivity::class.java)
            intent.putExtra("token", token)
            intent.putExtra("userId", userId)
            startActivity(intent)
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_virtual_pet")
            firebaseAnalytics.logEvent("navigate_to_virtual_pet", bundle)
        }
    }

    private fun retryFetchingData() {
        showLoading(true)
        profileViewModel.getDetailUser(token, userId)
        virtualPetViewModel.getVirtualPet(token, userId)
    }

    private fun showError(message: String) {
        if (isErrorDialogShown) return
        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.network_error_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)
            val descText = dialog.findViewById<TextView>(R.id.desc)
            val btnClose = dialog.findViewById<Button>(R.id.btnRetry)
            descText.text = message
            btnClose.setOnClickListener {
                dialog.dismiss()
                isErrorDialogShown = false
                retryFetchingData()
            }
            show()
        }
        isErrorDialogShown = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _homeFragment = null
    }

}