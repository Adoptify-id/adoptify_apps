package com.example.adoptify_core.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentBookmarkBinding
import com.example.core.data.Resource
import com.example.core.data.source.local.entity.PetEntity
import com.example.core.ui.FavoriteItemAdapter
import org.koin.android.viewmodel.ext.android.viewModel


class BookmarkFragment : Fragment() {

    private var _bookmarkFragment: FragmentBookmarkBinding? = null
    private val bookmarkFragment get() = _bookmarkFragment!!

    private var listFavorite: List<PetEntity> = listOf()
    private val bookmarkViewModel: BookmarkViewModel by viewModel()

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
        setupView()
        getFavorite()
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
            adapter = FavoriteItemAdapter(listFavorite) {}
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