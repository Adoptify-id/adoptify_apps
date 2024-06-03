package com.example.adoptify_core.ui.virtual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.model.AddVirtualPet
import com.example.core.domain.model.AddVirtualPetItem
import com.example.core.domain.model.VirtualPet
import com.example.core.domain.model.VirtualPetItem
import com.example.core.domain.usecase.MainUseCase
import kotlinx.coroutines.launch

class VirtualPetViewModel(private val mainUseCase: MainUseCase): ViewModel() {
    private val _virtualPet = MutableLiveData<Resource<List<VirtualPetItem>>>()
    val virtualPet: LiveData<Resource<List<VirtualPetItem>>> get() = _virtualPet

    private val _result = MutableLiveData<Resource<AddVirtualPet>>()
    val result: LiveData<Resource<AddVirtualPet>> get() = _result

   fun getVirtualPet(token: String, userId: Int) {
       _virtualPet.value = Resource.Loading(true)
       viewModelScope.launch {
           mainUseCase.getVirtualPet(token, userId).collect {
               _virtualPet.value = it
           }
       }
   }

    fun insertVirtualPet(token: String, data: AddVirtualPetItem) {
        _result.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.insertVirtualPet(token, data).collect {
                _result.value = it
            }
        }
    }
 }