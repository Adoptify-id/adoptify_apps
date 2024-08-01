package com.example.adoptify_core.ui.bookmark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentBookmarkBinding
import com.example.adoptify_core.ui.adopt.detail.DetailAdoptActivity
import com.example.core.data.Resource
import com.example.core.data.source.local.entity.PetEntity
import com.example.core.ui.FavoriteItemAdapter
import com.example.core.utils.SessionViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class BookmarkFragment : Fragment() {

    private var _bookmarkFragment: FragmentBookmarkBinding? = null
    private val bookmarkFragment get() = _bookmarkFragment!!

    private var listFavorite: List<PetEntity> = listOf()
    private var filteredData: List<PetEntity> = listOf()
    private val bookmarkViewModel: BookmarkViewModel by viewModel()
    private val sessionViewModel: SessionViewModel by viewModel()

    private var token = ""
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _bookmarkFragment = FragmentBookmarkBinding.inflate(inflater, container, false)
        return _bookmarkFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookmarkFragment.header.txtBookmark.text = requireActivity().getString(R.string.saved)

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val petId = data?.getIntExtra("PET_ID", -1) ?: return@registerForActivityResult
                val position = listFavorite.indexOfFirst { it.id == petId }
                if (position != -1) {
                    bookmarkFragment.rvFavorite.scrollToPosition(position)
                }
            }
        }

        observeData()
        setupView()
        getFavorite()
    }

    private fun observeData() {
        sessionViewModel.token.observe(viewLifecycleOwner) {
            token = it
        }
    }

    private fun setupView() {
        bookmarkFragment.header.btnShortcut.visibility = View.GONE
        bookmarkViewModel.getFavoritePet()
    }

    private fun getFavorite() {
        bookmarkViewModel.favorite.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                    showContent(true)
                }

                is Resource.Success -> {
                    showLoading(false)
                    listFavorite = it.data
                    if (listFavorite.isNotEmpty()) {
                        showContent(false)
                        filteredData = listFavorite.filter { it.isAdopt == false }
                        showRecyclerView()
                    } else {
                        showContent(true)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    showContent(true)
                    Log.d("BookmarkFragment", "error: ${it.message}")
                }
            }
        }
    }

    private fun showRecyclerView() {
        bookmarkFragment.rvFavorite.apply {
            adapter = FavoriteItemAdapter(filteredData) { pet, imageView ->
                val intent = Intent(requireContext(), DetailAdoptActivity::class.java)
                intent.putExtra("PET_ID", pet.id)
                intent.putExtra("TOKEN", token)
                intent.putExtra("TRANSITION_NAME", imageView.transitionName)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    imageView,
                    imageView.transitionName
                )
                startForResult.launch(intent, options)
            }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showContent(isShowing: Boolean) {
        bookmarkFragment.contentNull.layout.visibility = if (isShowing) View.VISIBLE else View.GONE
        bookmarkFragment.contentNull.btnClose.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        bookmarkFragment.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bookmarkFragment = null
    }
}