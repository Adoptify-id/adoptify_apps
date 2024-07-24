package com.example.adoptify_core.ui.foster.dashboard

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentDashboardBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.foster.detail.DetailFosterActivity
import com.example.adoptify_core.ui.foster.profile.DetailProfileFosterActivity
import com.example.adoptify_core.ui.foster.submission.SubmissionFosterActivity
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.adoptify_core.ui.profile.ProfileViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.ListPetItem
import com.example.core.ui.FosterItemAdapter
import com.example.core.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates


class DashboardFragment : Fragment() {

    private var _dashboardFragment: FragmentDashboardBinding? = null

    private val dashboardFragment get() = _dashboardFragment!!

    private val loginViewModel: LoginViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModel()

    private val adoptViewModel: AdoptViewModel by viewModel()

    private val profileViewModel: ProfileViewModel by viewModel()

    private val sessionManager: SessionManager by inject()

    private var token = ""
    private var userId by Delegates.notNull<Int>()

    private var isTokenAvailable = false
    private var isUserIdAvailable = false

    private var dataPet: List<DataAdopt> = listOf()

    private lateinit var itemAdapter: FosterItemAdapter

    private val combinedData = mutableListOf<ListPetItem>()

    private var bottomDialog: BottomSheetDialog? = null

    private lateinit var startForResult: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _dashboardFragment = FragmentDashboardBinding.inflate(inflater, container, false)
        return _dashboardFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val petId = data?.getIntExtra("PET_ID", -1) ?: return@registerForActivityResult
                val position = dataPet.indexOfFirst { it.petId == petId }
                if (position != -1) {
                    dashboardFragment.rvPet.scrollToPosition(position)
                }
            }
        }

        setupView()
        initData()
        getToken()
        getUserId()
        getName()
        setupListener()

        dashboardFragment.swipeRefresh.apply {
            setOnRefreshListener {
                getToken()
                getUserId()
            }
            setColorSchemeColors(resources.getColor(R.color.primary_color_foster))
        }

        Log.d("MainActivity", "SessionManager initialized: $sessionManager")
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
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        dashboardFragment.apply {
            btnLogout.setOnClickListener {
                bottomDialog = BottomSheetDialog(requireContext()).apply {
                    val view = layoutInflater.inflate(R.layout.bottom_sheet_logout, null)
                    val btnClose = view.findViewById<Button>(R.id.btnBack)
                    val btnLogout = view.findViewById<Button>(R.id.btnLogout)
                    btnLogout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_color_foster)
                    btnClose.setOnClickListener { dismiss() }
                    btnLogout.setOnClickListener {
                        val optionsLogout = ActivityOptionsCompat.makeCustomAnimation(
                            requireContext(),
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent, optionsLogout.toBundle())
                        activity?.finish()
                        profileViewModel.deleteSession()
                    }
                    setCancelable(false)
                    setContentView(view)
                    show()
                }
            }
            card.btnProfile.setOnClickListener {
                val intent = Intent(requireContext(), DetailProfileFosterActivity::class.java)
                intent.putExtra("TOKEN", token)
                intent.putExtra("USER_ID", userId)
                startActivity(intent, options.toBundle())
            }
            card.btnTransaction.setOnClickListener {
                startActivity(Intent(requireContext(), SubmissionFosterActivity::class.java), options.toBundle())
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
                    dashboardFragment.swipeRefresh.isRefreshing = false
                    if (isTokenAvailable && isUserIdAvailable) {
                        getListPet()
                        getProfileUser()
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
                    dashboardFragment.swipeRefresh.isRefreshing = false
                    if (isTokenAvailable && isUserIdAvailable) {
                        getListPet()
                        getProfileUser()
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
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    dataPet = it.data
                    if (dataPet.isNotEmpty()) {
                        showContent(false)
                        combinedData.addAll(dataPet.map { data -> ListPetItem.PetItem(data) })
                        updateCombinedData()
                        Log.d("Home", "data: ${it.data}")
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showContent(true)
                    showLoading(false)
                    Log.d("Home", "error: ${it.message}")
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
                    it.data.data?.map {
                        val imageUrl =
                            "https://storage.googleapis.com/bucket-adoptify/imagesUser/${it?.foto}"
                        Glide.with(requireActivity())
                            .load(imageUrl)
                            .placeholder(R.drawable.dummy_profile)
                            .into(dashboardFragment.card.profileUser)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
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
        itemAdapter = FosterItemAdapter(data) { pet, imageView ->
            val intent = Intent(requireContext(), DetailFosterActivity::class.java)
            intent.putExtra("PET_ID", pet.petId)
            intent.putExtra("TOKEN", token)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("TRANSITION_NAME", imageView.transitionName)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                imageView,
                imageView.transitionName
            )
            startForResult.launch(intent, options)
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

    private fun showContent(isShowing: Boolean) {
        dashboardFragment.contentNull.apply {
            layout.visibility = if (isShowing) View.VISIBLE else View.GONE
            btnClose.setOnClickListener { }
            txtTitle.setTextColor(resources.getColor(R.color.primary_color_foster))
            btnClose.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.primary_color_foster)
            txtDesc.text =
                "Maaf, data pet Anda tidak tersedia. Coba muat ulang atau periksa kembali nanti."

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dashboardFragment = null
        bottomDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        bottomDialog?.dismiss()
    }
}