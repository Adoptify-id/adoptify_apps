package com.example.adoptify_core.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.launch

class MainViewModel(private val authUseCase: AuthUseCase) : ViewModel() {
    private val _name = MutableLiveData<Resource<String>>()
    val name: LiveData<Resource<String>> get() = _name

    private val _userId = MutableLiveData<Resource<Int>>()

    val userId: LiveData<Resource<Int>> get() = _userId

    fun getName() {
        viewModelScope.launch {
            _name.value = Resource.Loading(true)
            authUseCase.getUsername().collect {
                _name.value = Resource.Success(it)
                Log.d("Home", "getName: $it")
            }
        }
    }

    fun getUserId() {
        viewModelScope.launch {
            _userId.value = Resource.Loading(true)
            authUseCase.getUserId().collect {
                _userId.value = Resource.Success(it)
                Log.d("Home", "id: $it")
            }
        }
    }
}