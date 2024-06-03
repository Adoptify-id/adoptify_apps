package com.example.adoptify_core.ui.foster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.usecase.MainUseCase
import kotlinx.coroutines.launch

class FosterViewModel(private val mainUseCase: MainUseCase) : ViewModel() {
    private val _result = MutableLiveData<Resource<PetAdopt>>()

    val result: LiveData<Resource<PetAdopt>> get() = _result

    private val _detail = MutableLiveData<Resource<PetAdopt>>()

    val detail: LiveData<Resource<PetAdopt>> get() = _detail



    fun insertAdopt(token: String, data: PetAdoptItem) {
        _result.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.insertPetAdopt(token, data).collect {
                _result.value = it
            }
        }
    }

    fun updatePetAdopt(token: String, petId: Int, data: PetAdoptItem) {
        _detail.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.updatePet(token, petId, data).collect {
                _detail.value = it
            }
        }
    }
}