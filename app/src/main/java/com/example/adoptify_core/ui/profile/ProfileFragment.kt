package com.example.adoptify_core.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentProfileBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.medical.MedicalRecordActivity
import com.example.core.data.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class ProfileFragment : Fragment() {

    private var _profileFragment: FragmentProfileBinding? = null
    private val profileFragment get() = _profileFragment!!

    private val profileViewModel: ProfileViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModel()

    private val loginViewModel: LoginViewModel by viewModel()

    private var token = ""
    private var userId by Delegates.notNull<Int>()

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
        getName()
        getToken()
        getUserId()
        setupListener()
    }

    private fun initUsername() {
        mainViewModel.getName()
        mainViewModel.getUserId()
        loginViewModel.getSession()
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    token = it.data
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
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    userId = it.data
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("Home", "error: ${it.message}")
                }
            }
        }
    }

    private fun setupListener()  {
        profileFragment.card.icEdit.setOnClickListener {
            val intent = Intent(requireContext(), DetailProfileActivity::class.java)
            intent.putExtra("token", token)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        profileFragment.icLogout.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            profileViewModel.deleteSession()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _profileFragment = null
    }

    private fun showLoading(isLoading: Boolean) {
        profileFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}