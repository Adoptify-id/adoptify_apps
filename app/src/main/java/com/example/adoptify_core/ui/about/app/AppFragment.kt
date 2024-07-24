package com.example.adoptify_core.ui.about.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adoptify_core.databinding.FragmentAppBinding

class AppFragment : Fragment() {

    private var _appFragment: FragmentAppBinding? = null

    private val appFragment get() = _appFragment!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _appFragment = FragmentAppBinding.inflate(inflater, container, false)
        return _appFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
    }

    private fun setupListener() {
        appFragment.apply {
            linkedin.setOnClickListener { openUrl("https://www.linkedin.com/company/adoptify-id/") }
            tiktok.setOnClickListener { openUrl("https://www.tiktok.com/@adoptify.id?_t=8oD9vjrNSj4&_r=1") }
            instagram.setOnClickListener { openUrl("https://www.instagram.com/adoptify.id?igsh=MXRhZnp4cGx3bXExMg==") }
            web.setOnClickListener {  }
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _appFragment = null
    }

}