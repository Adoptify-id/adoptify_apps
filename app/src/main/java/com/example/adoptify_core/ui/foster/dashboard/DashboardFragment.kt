package com.example.adoptify_core.ui.foster.dashboard

import android.animation.LayoutTransition
import android.content.Intent
import android.graphics.text.TextRunShaper
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.databinding.FragmentDashboardBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.foster.detail.DetailFosterActivity
import com.example.adoptify_core.ui.foster.profile.DetailProfileFosterActivity
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.ListPetItem
import com.example.core.ui.FosterItemAdapter
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.exp
import kotlin.properties.Delegates


class DashboardFragment : Fragment() {

    private var _dashboardFragment: FragmentDashboardBinding? = null

    private val dashboardFragment get() = _dashboardFragment!!

    private val loginViewModel: LoginViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModel()

    private val adoptViewModel: AdoptViewModel by viewModel()

    private val profileViewModel: ProfileViewModel by viewModel()

    private var token = ""
    private var userId by Delegates.notNull<Int>()

    private var isTokenAvailable = false
    private var isUserIdAvailable = false

    private var dataPet: List<DataAdopt> = listOf()

    private lateinit var itemAdapter: FosterItemAdapter

    private val combinedData = mutableListOf<ListPetItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _dashboardFragment = FragmentDashboardBinding.inflate(inflater, container, false)
        return _dashboardFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        initData()
        getToken()
        getUserId()
        getName()
        setupListener()
    }

    private fun setupView() {
        dashboardFragment.apply {
            card.layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            card.icNext.setOnClickListener { expand() }
            card.layout.setOnClickListener { expand() }
        }
    }

    private fun initData() {
        mainViewModel.getName()
        loginViewModel.getSession()
        mainViewModel.getUserId()
    }

    private fun setupListener() {
        dashboardFragment.apply {
            btnLogout.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
                profileViewModel.deleteSession()
            }
            card.btnProfile.setOnClickListener {
                val intent = Intent(requireContext(), DetailProfileFosterActivity::class.java)
                intent.putExtra("TOKEN", token)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }
        }
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    token = it.data
                    isTokenAvailable = true
                    if (isTokenAvailable && isUserIdAvailable) {
                        getListPet()
                    }
                    Log.d("HomeFragment", "check: $token")
                }

                is Resource.Error -> {}
            }
        }
    }

    private fun getUserId() {
        mainViewModel.userId.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    userId = it.data
                    isUserIdAvailable = true
                    if (isTokenAvailable && isUserIdAvailable) {
                        getListPet()
                    }
                    dashboardFragment.card.fosterId.text = "#FosterID · ${it.data}"
                    Log.d("Foster", "data: ${it.data}")
                }

                is Resource.Error -> {
                    Log.d("Foster", "error: ${it.message}")
                }
            }
        }
    }

    private fun getName() {
        mainViewModel.name.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    showLoading(false)
                    val name = it.data.split(" ").joinToString { it.capitalize() }
                    dashboardFragment.card.username.text = name
                    Log.d("Home", "username: $name")
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("Home", "error: ${it.message}")
                }
            }
        }
    }

    private fun getListPet() {
        adoptViewModel.getPetByUser(token, userId)
        adoptViewModel.data.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> { showLoading(true) }

                is Resource.Success -> {
                    showLoading(false)
                    dataPet = it.data
                    combinedData.addAll(dataPet.map { data -> ListPetItem.PetItem(data) })
                    updateCombinedData()
                    Log.d("Home", "data: ${it.data}")
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("Home", "error: ${it.message}")
                }
            }
        }
    }

    private fun updateCombinedData() {
        combinedData.clear()
        combinedData.addAll(dataPet.map { data -> ListPetItem.PetItem(data) })
        showRecyclerView()
    }

    private fun showRecyclerView() {
        val data = sortDataPet()
        itemAdapter = FosterItemAdapter(data) {
            val intent = Intent(requireContext(), DetailFosterActivity::class.java)
            intent.putExtra("PET_ID", it.petId)
            intent.putExtra("TOKEN", token)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
        dashboardFragment.rvPet.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@DashboardFragment.itemAdapter
        }

    }

    private fun sortDataPet(): List<ListPetItem> {
        val groupedData = mutableListOf<ListPetItem>()
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fullDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))

        val sortedData = combinedData.sortedByDescending {
            when (it) {
                is ListPetItem.PetItem -> parseDateToLong(it.data.createdAt)
                else -> 0L
            }
        }
        var lastDate = ""

        sortedData.forEach { item ->
            val itemDateStr = when (item) {
                is ListPetItem.PetItem -> item.data.createdAt?.split("T")?.get(0)
                else -> null
            }

            val itemDate = dateFormat.parse(itemDateStr)
            val header = when {
                dateFormat.format(today.time) == itemDateStr -> "Hari ini"
                dateFormat.format(yesterday.time) == itemDateStr -> "Kemarin"
                else -> fullDateFormat.format(itemDate)
            }

            if (header != lastDate) {
                groupedData.add(ListPetItem.HeaderItem(header))
                lastDate = header
            }

            groupedData.add(item)
        }
        return groupedData
    }

    private fun parseDateToLong(dateString: String?): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun showLoading(isLoading: Boolean) {
        dashboardFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun expand() {
        val isVisible = dashboardFragment.card.listButton.visibility == View.VISIBLE
        val newVisible = if (isVisible) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(dashboardFragment.card.layout, AutoTransition())
        dashboardFragment.card.listButton.visibility = newVisible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dashboardFragment = null
    }

}