package com.example.adoptify_core.ui.adopt


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.usecase.MainUseCase
import kotlinx.coroutines.launch

class AdoptViewModel(private val mainUseCase: MainUseCase): ViewModel() {
    private val _data = MutableLiveData<Resource<List<DataAdopt>>>()

    val data: LiveData<Resource<List<DataAdopt>>> get() = _data

    private val _detail = MutableLiveData<Resource<PetAdopt>>()

    val detail: LiveData<Resource<PetAdopt>> get() = _detail

    private val _update = MutableLiveData<Resource<PetAdopt>>()

    val update: LiveData<Resource<PetAdopt>> get() = _update


    fun getListPet(token: String) {
        _data.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getListPet(token).collect {
                _data.value = it
            }
        }
    }

    fun getDetailPet(token: String, petId: Int) {
        _detail.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getDetailPet(token, petId).collect {
                _detail.value = it
            }
        }
    }

    fun getPetByUser(token: String, userId: Int) {
        _data.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getPetByUser(token, userId).collect {
                _data.value = it
            }
        }
    }

}