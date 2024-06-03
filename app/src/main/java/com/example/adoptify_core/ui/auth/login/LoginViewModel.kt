package com.example.adoptify_core.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.model.Login
import com.example.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.launch

class LoginViewModel(private val authUseCase: AuthUseCase) : ViewModel() {

    private val _result = MutableLiveData<Resource<Login>>()
    val result: LiveData<Resource<Login>> get() = _result

    private val _token = MutableLiveData<Resource<String>>()
    val token: LiveData<Resource<String>> get() = _token

    private val _roleId = MutableLiveData<Resource<Int>>()
    val roleId: LiveData<Resource<Int>> get() = _roleId

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _result.value = Resource.Loading(true)
            authUseCase.login(username, password).collect {
                _result.value = it
            }
        }
    }

    fun saveSession(token: String, username: String, userId: Int, roleId: Int) {
        viewModelScope.launch {
            authUseCase.saveSession(token, username, userId, roleId)
        }
    }

    fun getSession() {
        viewModelScope.launch {
            _token.value = Resource.Loading(true)
            authUseCase.getAuthToken().collect {
                _token.value = Resource.Success(it)
            }
        }
    }

    fun getRoleId() {
        viewModelScope.launch {
            _roleId.value = Resource.Loading(true)
            authUseCase.getRoleId().collect {
                _roleId.value = Resource.Success(it)
                Log.d("Home", "role: $it")
            }
        }
    }
}