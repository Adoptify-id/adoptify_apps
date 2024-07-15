package com.example.adoptify_core.ui.adopt.process.cat

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityProcessAdoptBinding
import com.example.adoptify_core.ui.adopt.SharedDataViewModel
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.utils.ForceLogout

class ProcessAdoptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProcessAdoptBinding
    private lateinit var sharedDataViewModel: SharedDataViewModel

    private var logoutDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessAdoptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedDataViewModel = ViewModelProvider(this)[SharedDataViewModel::class.java]

        val adapter = PageAdoptAdapter(this)
        val viewPager: ViewPager2 = binding.processViewPager
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        val petId = intent?.getIntExtra("PET_ID", 0)
        Log.d("ProcessAdopt", "petId: $petId")
        if (petId != 0) {
            sharedDataViewModel.petId.value = petId
        }
        forceLogout()
    }

    private fun forceLogout() {
        ForceLogout.logoutLiveData.observe(this) {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        logoutDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.modal_session_expired)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //set width height card
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val btnLogin = findViewById<Button>(R.id.btnReload)

            btnLogin.setOnClickListener { navigateToLogin() }
            show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        logoutDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        logoutDialog?.dismiss()
    }
}