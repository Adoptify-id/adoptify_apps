package com.example.adoptify_core.ui.auth.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityLoginBinding
import com.example.adoptify_core.databinding.AlertDialogBinding
import com.example.adoptify_core.ui.auth.register.RegisterActivity
import com.example.adoptify_core.ui.foster.FosterActivity
import com.example.adoptify_core.ui.main.MainActivity
import com.example.core.data.Resource
import com.example.core.data.source.remote.network.ApiResponse
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()

    private var roleId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginResult()
        getRole()
        loginViewModel.getRoleId()
        validateForm()
        setupListener()
    }

    private fun setupListener() {
        binding.apply {
            btnLogin.setOnClickListener { loginHandler() }
            txtRegister.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        RegisterActivity::class.java
                    )
                )
            }
            usernameEditText.addTextChangedListener(textWatcher)
            passwordEditText.addTextChangedListener(textWatcher)
        }
    }

    private fun validateForm() {
        binding.apply {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            val isFormValid = username.isNotEmpty() && password.isNotEmpty()
            btnLogin.isEnabled = isFormValid
            btnLogin.setBackgroundColor(
                ContextCompat.getColor(
                    this@LoginActivity,
                    if (isFormValid) R.color.primaryColor else R.color.btn_disabled
                )
            )
        }
    }

    private fun loginHandler() {
        val username = binding.usernameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        loginViewModel.loginUser(username, password)
    }

    private fun loginResult() {
        loginViewModel.result.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                }

                is Resource.Success -> {
                    val token = it.data.accessToken
                    val username = it.data.username
                    val userId = it.data.userId
                    val roleId = it.data.roleId
                    showLoading(false)
                    if (token != null) {
                        getSessionUser(token, username, userId, roleId)
                    }
                }

                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Proses login gagal", R.drawable.alert_failed)
                    Log.e("LoginActivity", "error: ${it.message}")
                }
            }
        }
    }

    //get and save token for login
    private fun getSessionUser(token: String, username: String, userId: Int, roleId: Int) {
        loginViewModel.saveSession(token, username, userId, roleId)

        if (roleId == 1) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@LoginActivity, FosterActivity::class.java))
            finish()
        }

    }

    private fun getRole() {
        loginViewModel.roleId.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    roleId = it.data
                    Log.d("LoginActivity", "role id: $roleId")
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("LoginActivity", "error : ${it.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    //text watcher for edit text
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateForm()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                val lowerCaseText = it.toString().lowercase()
                if (lowerCaseText != it.toString()) {
                    binding.usernameEditText.removeTextChangedListener(this)
                    binding.usernameEditText.setText(lowerCaseText)
                    binding.usernameEditText.setSelection(lowerCaseText.length)
                    binding.usernameEditText.addTextChangedListener(this)
                }
            }
        }
    }

    private fun popUpDialog(title: String, desc: String, image: Int) {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.alert_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            //set width height card
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)

            val imageView = dialog.findViewById<ImageView>(R.id.img_alert)
            val titleText = dialog.findViewById<TextView>(R.id.title_alert)
            val descText = dialog.findViewById<TextView>(R.id.desc_alert)
            val btnClose = dialog.findViewById<Button>(R.id.btnClose)

            imageView.setImageDrawable(ContextCompat.getDrawable(this@LoginActivity, image))
            titleText.text = title
            descText.text = desc
            btnClose.setOnClickListener { dismiss() }
            show()
        }
    }
}
