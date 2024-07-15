package com.example.adoptify_core.ui.adopt.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.databinding.FragmentListPetBinding
import com.example.adoptify_core.ui.adopt.AdoptViewModel
import com.example.adoptify_core.ui.adopt.detail.DetailAdoptActivity
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.ui.PetItemAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class ListPetFragment : Fragment() {

    private var _listFragment: FragmentListPetBinding? = null
    private val listFragment get() = _listFragment!!

    private val adoptViewModel: AdoptViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private var token = ""
    private var position: Int = 0
    private var dataPet: List<DataAdopt> = listOf()
    private var filteredData: List<DataAdopt> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _listFragment = FragmentListPetBinding.inflate(inflater, container, false)
        return _listFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        getToken()
        setupView()

    }

    private fun setupView() {
        if (position == 1) {
            getListPet("Kucing")
        } else {
            getListPet("Anjing")
        }
    }

    private fun initData() {
        arguments?.let {
            position = it.getInt(ARG_POSITION, 0)
        }
        loginViewModel.getSession()
    }

    private fun showRecyclerView() {
        listFragment.rvPet.apply {
            adapter = PetItemAdapter(filteredData) {
                val intent = Intent(requireContext(), DetailAdoptActivity::class.java)
                intent.putExtra("PET_ID", it.petId)
                intent.putExtra("TOKEN", token)
                startActivity(intent)
            }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun showLoading(isLoading: Boolean) {
        listFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    private fun getListPet(category: String) {
        adoptViewModel.data.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    dataPet = it.data
                    Log.d("AdoptFragment", "sebelum filter: ${it.data}")
                    if (dataPet.isNotEmpty()) {
                        showContent(false)
                        filteredData = dataPet.filter { it.kategori == category && it.isAdopt == false }
                        Log.d("AdoptFragment", "data: $filteredData")
                        showRecyclerView()
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    showContent(true)
                    Log.d("AdoptFragment", "error: ${it.message}")
                }
            }
        }
    }

    private fun showContent(isShowing: Boolean) {
        listFragment.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE
        listFragment.contentNull.txtDesc.text = "Maaf, data pet Anda tidak tersedia. Coba muat ulang atau periksa kembali nanti."

        listFragment.contentNull.btnClose.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _listFragment = null
    }

    companion object {
        const val ARG_POSITION = "arg_position"
    }
}