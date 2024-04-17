package com.example.adoptify_core.ui.medical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.ActivityMedicalRecordBinding

class MedicalRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_record)

        val fragment = MedicalRecordFragment()
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.fragment_container, fragment)
        fragmentManager.commit()
    }
}