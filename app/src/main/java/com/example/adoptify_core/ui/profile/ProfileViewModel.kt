package com.example.adoptify_core.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataUser
import com.example.core.data.source.remote.response.UserResponse
import com.example.core.domain.model.DetailUser
import com.example.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(private val authUseCase: AuthUseCase): ViewModel() {

    private val _result = MutableLiveData<Resource<DetailUser>>()

    val result: LiveData<Resource<DetailUser>> get() = _result

    private val _detail = MutableLiveData<Resource<DetailUser>>()

    val detail: LiveData<Resource<DetailUser>> get() = _detail

    fun deleteSession() {
        viewModelScope.launch {
            authUseCase.deleteSession()
        }
    }

    fun updateProfile(token: String, userId: Int, data: DataUser) {
        _result.value = Resource.Loading(true)
        viewModelScope.launch {
            authUseCase.updateUser(token, userId, data).collect {
                _result.value = it
            }
        }
    }

    fun getDetailUser(token: String, userId: Int) {
        _detail.value = Resource.Loading(true)
        viewModelScope.launch {
            authUseCase.getDetailUser(token, userId).collect {
                _detail.value = it
            }
        }
    }
}