package com.example.adoptify_core.ui.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentBookmarkBinding


class BookmarkFragment : Fragment() {

    private var _bookmarkFragment: FragmentBookmarkBinding? = null
    private val bookmarkFragment get() = _bookmarkFragment!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bookmarkFragment = null
    }
}