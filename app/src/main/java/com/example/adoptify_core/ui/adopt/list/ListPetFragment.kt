package com.example.adoptify_core.ui.adopt.list

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentListPetBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.adopt.detail.DetailAdoptActivity
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.ui.PetItemAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.viewmodel.ext.android.viewModel

class ListPetFragment : Fragment() {

    private var _listFragment: FragmentListPetBinding? = null
    private val listFragment get() = _listFragment!!

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private var token = ""
    private var category: String = ""
    private var dataPet: List<DataAdopt> = listOf()
    private var filteredData: List<DataAdopt> = listOf()

    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private var isErrorDialogShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _listFragment = FragmentListPetBinding.inflate(inflater, container, false)
        return _listFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        shimmerFrameLayout = listFragment.shimmerLayout
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val petId = data?.getIntExtra("PET_ID", -1) ?: return@registerForActivityResult
                    val position = filteredData.indexOfFirst { it.petId == petId }
                    if (position != -1) {
                        listFragment.rvPet.scrollToPosition(position)
                    }
                }
            }

        initData()
        getToken()
        getListPet()
    }

    override fun onResume() {
        super.onResume()
        getListPet()
    }

    private fun initData() {
        arguments?.let {
            category = it.getString(ARG_CATEGORY, "")
        }
        loginViewModel.getSession()
    }

    private fun showRecyclerView() {
        listFragment.rvPet.apply {
            adapter = PetItemAdapter(filteredData) { pet, imageView ->
                val intent = Intent(requireContext(), DetailAdoptActivity::class.java)
                intent.putExtra("PET_ID", pet.petId)
                intent.putExtra("TOKEN", token)
                intent.putExtra("TRANSITION_NAME", imageView.transitionName)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    imageView,
                    imageView.transitionName
                )

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation")
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "btn_detail_pet_adopt")
                firebaseAnalytics.logEvent("navigate_to_detail_pet_adopt", bundle)

                startForResult.launch(intent, options)
            }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            showContent(false)
            listFragment.rvPet.visibility = View.GONE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            listFragment.rvPet.visibility = View.VISIBLE
        }
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    token = it.data
                    adoptViewModel.getListPet(token)
                }

                is Resource.Error -> {
                    showLoading(false)
                    Log.d("AdoptFragment", "error: ${it.message}")
                }
            }
        }
    }

    private fun getListPet() {
        adoptViewModel.data.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    dataPet = it.data

                    filteredData = dataPet.filter { pet -> pet.kategori == category && !pet.isAdopt!! }
                    if (filteredData.isNotEmpty()) {
                        showContent(false)
                        showRecyclerView()
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
                    Log.d("AdoptFragment", "error: ${it.message}")
                }
            }
        }
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

    private fun retryFetchingData() {
        showLoading(true)
        adoptViewModel.getListPet(token)
    }

    private fun showContent(isShowing: Boolean) {
        listFragment.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE
        listFragment.contentNull.txtDesc.text =
            "Maaf, data pet Anda tidak tersedia. Coba muat ulang atau periksa kembali nanti."

        listFragment.contentNull.btnClose.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _listFragment = null
    }

    companion object {
        const val ARG_CATEGORY = "arg_category"
        fun newInstance(category: String): ListPetFragment {
            val fragment = ListPetFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }
}