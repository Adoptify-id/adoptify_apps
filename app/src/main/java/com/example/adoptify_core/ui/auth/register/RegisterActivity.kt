package com.example.adoptify_core.ui.auth.register

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityRegisterBinding
import com.example.adoptify_core.databinding.AlertDialogBinding
import com.example.adoptify_core.ui.auth.login.LoginActivity
import com.example.core.data.Resource
import com.example.core.domain.model.User
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerResult()
        validateForm()
        setupView()

    }

    private fun setupView() {
        binding.apply {
            usernameEditText.addTextChangedListener(textWatcher)
            emailEditText.addTextChangedListener(textWatcher)
            telephoneEditText.addTextChangedListener(textWatcher)
            passwordEditText.addTextChangedListener(textWatcher)
            confirmPasswordEditText.addTextChangedListener(textWatcher)

            btnRegister.setOnClickListener { registerHandler() }
            btnBack.setOnClickListener { onBackPressed() }
            txtLogin.setOnClickListener { startActivity(Intent(this@RegisterActivity, LoginActivity::class.java)) }
        }
    }



    private fun registerHandler() {
        val username = binding.usernameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.telephoneEditText.text.toString()
        val password = binding.confirmPasswordEditText.toString()
        val data = User(username, email, phone, password)

        registerViewModel.registerUser(data)
    }

    private fun registerResult() {
        registerViewModel.result.observe(this) {
            when(it) {
                is Resource.Loading -> { showLoading(true) }
                is Resource.Success -> {
                    showLoading(false)
                    it.data.message
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
//                    popUpDialog("Yeiy!", "Proses register berhasil", R.drawable.alert_success, intent)
                    Log.d("RegisterActivity", "result: ${it.data.message}")
                }
                is Resource.Error -> {
                    showLoading(false)
                    popUpDialog("Yah!", "Proses register gagal", R.drawable.alert_failed)
                    Log.d("RegisterActivity", "error : ${it.message}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun validateForm() {
        binding.apply {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val telp = telephoneEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            val isPasswordValid = password == confirmPassword
            val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

            val isFormValid = username.isNotEmpty() && isEmailValid && telp.isNotEmpty() && isPasswordValid
            btnRegister.isEnabled = isFormValid
            btnRegister.setBackgroundColor(
                ContextCompat.getColor(
                    this@RegisterActivity,
                    if (isFormValid) R.color.primaryColor else R.color.btn_disabled
                )
            )
        }
    }

    //text watcher for edit text
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { validateForm() }
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

            imageView.setImageDrawable(ContextCompat.getDrawable(this@RegisterActivity, image))
            titleText.text = title
            descText.text = desc
            btnClose.setOnClickListener { dismiss() }
            show()
        }
    }

}
