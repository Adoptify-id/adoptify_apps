package com.example.adoptify_core.ui.auth.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.model.Register
import com.example.core.domain.model.User
import com.example.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewModel(private val authUseCase: AuthUseCase) : ViewModel() {
    private val _result = MutableLiveData<Resource<Register>>()
    val result: LiveData<Resource<Register>> get() = _result

    fun registerUser(user: User) {
        viewModelScope.launch {
            authUseCase.registerUser(user).collect { result ->
                _result.value = result
            }
        }
    }
}