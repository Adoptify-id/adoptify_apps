package com.example.core.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.data.source.local.datastore.UserPreferences
import kotlinx.coroutines.launch

class SessionViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    val token: LiveData<String> = userPreferences.getSession().asLiveData()
    val userId: LiveData<Int> = userPreferences.getUserId().asLiveData()

    init {
        viewModelScope.launch {
            userPreferences.getSession().collect {
                Log.d("SessionViewModel", "token: $it")
            }

            userPreferences.getUserId().collect {
                Log.d("SessionViewModel" ,"userId: $it ")
            }
        }
    }
}