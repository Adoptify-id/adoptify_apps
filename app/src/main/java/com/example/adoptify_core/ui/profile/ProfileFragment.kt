package com.example.adoptify_core.ui.profile

import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentProfileBinding
import com.example.adoptify_core.ui.adopt.submission.SubmissionAdoptActivity
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.coming.ComingSoonActivity
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.core.data.Resource
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _profileFragment: FragmentProfileBinding? = null
    private val profileFragment get() = _profileFragment!!

    private val profileViewModel: ProfileViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModel()

    private val loginViewModel: LoginViewModel by viewModel()

    private var bottomDialog: BottomSheetDialog? = null

    private var token: String? = null
    private var userId: Int? = null

    private var isTokenAvailable = false
    private var isUserIdAvailable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _profileFragment = FragmentProfileBinding.inflate(inflater, container, false)
        return _profileFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUsername()
        getToken()
        getUserId()
        getName()
        setupListener()
    }

    private fun initUsername() {
        mainViewModel.getName()
        mainViewModel.getUserId()
        loginViewModel.getSession()
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    token = it.data
                    isTokenAvailable = true
                    if (isTokenAvailable && isUserIdAvailable) {
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
                    val name = it.data.split(" ").joinToString { it.capitalize() }
                    profileFragment.card.username.text = name
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
                    if (isTokenAvailable && isUserIdAvailable) {
                        getProfileUser()
                    }
                    profileFragment.card.userId.text = "#UserID · $userId"
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("Home", "error: ${it.message}")
                }
            }
        }
    }

    private fun getProfileUser() {
        profileViewModel.getDetailUser(token!!, userId!!)
        profileViewModel.detail.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {showLoading(true)}
                is Resource.Success -> {
                    showLoading(false)
                    it.data.data?.map {
                        val imageUrl = "https://storage.googleapis.com/bucket-adoptify/imagesUser/${it?.foto}"
                        Glide.with(requireActivity())
                            .load(imageUrl)
                            .placeholder(R.drawable.dummy_profile)
                            .into(profileFragment.card.profileUser)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun setupListener() {
        profileFragment.apply {
            card.icEdit.setOnClickListener {
                val intent = Intent(requireContext(), DetailProfileActivity::class.java)
                intent.putExtra("token", token)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }
            card.layout.setOnClickListener { expand() }
            icLogout.setOnClickListener {
               bottomDialog =  BottomSheetDialog(requireContext()).apply {
                   val view = layoutInflater.inflate(R.layout.bottom_sheet_logout, null)
                   val btnClose = view.findViewById<Button>(R.id.btnBack)
                   val btnLogout = view.findViewById<Button>(R.id.btnLogout)
                   btnClose.setOnClickListener { dismiss() }
                   btnLogout.setOnClickListener {
                       val intent = Intent(requireContext(), LoginActivity::class.java)
                       intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                       startActivity(intent)
                       activity?.finish()
                       profileViewModel.deleteSession()
                   }
                   setCancelable(false)
                   setContentView(view)
                   show()
               }
            }
            card.btnReward.setOnClickListener { startActivity(Intent(requireContext(), ComingSoonActivity::class.java)) }
            card.btnTransaction.setOnClickListener { startActivity(Intent(requireContext(), SubmissionAdoptActivity::class.java)) }
        }
    }

    private fun expand() {
        val isVisible = profileFragment.card.listButton.visibility == View.VISIBLE
        val newVisible = if (isVisible) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(profileFragment.card.layout, AutoTransition())
        profileFragment.card.listButton.visibility = newVisible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _profileFragment = null
        bottomDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        bottomDialog?.dismiss()
    }

    private fun showLoading(isLoading: Boolean) {
        profileFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}