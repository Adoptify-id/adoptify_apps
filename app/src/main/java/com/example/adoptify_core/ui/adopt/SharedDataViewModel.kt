package com.example.adoptify_core.ui.adopt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDataViewModel : ViewModel() {
    val personalData = MutableLiveData<PersonalData>()
    val submissionData = MutableLiveData<SubmissionData>()
    val secondSubmissionData = MutableLiveData<SecondSubmissionData>()
    val petId = MutableLiveData<Int>()
}

data class PersonalData(
    val name: String = "",
    val age: Int,
    val telephone: String = "",
    val address: String = "",
    val job: String = "",
    val range: String = "",
    val medsos: String = "",
    val image: String? = ""
)

data class SubmissionData(
    val reason: String = "",
    val hope: String = "",
    val maintain: String = "",
    val beside: String = "",
    val vaccine: String = "",
    val adopter: String = ""
)

data class SecondSubmissionData(
    val food: String,
    val merk: String,
    val drink: String,
    val vaccine: String,
    val medicine: String,
    val move: String,
    val selectedCheckBoxes: String,
    val radioCage: String,
    val radioPrivacy: String,
    val radioNews: String
)