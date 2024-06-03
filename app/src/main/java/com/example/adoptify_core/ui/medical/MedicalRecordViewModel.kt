package com.example.adoptify_core.ui.medical

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.DataItem
import com.example.core.data.source.remote.response.VaksinasiItem
import com.example.core.domain.model.AddVaksinasiPet
import com.example.core.domain.model.MedicalItem
import com.example.core.domain.model.MedicalRecord
import com.example.core.domain.model.VaksinasiData
import com.example.core.domain.usecase.MainUseCase
import kotlinx.coroutines.launch

class MedicalRecordViewModel(private val mainUseCase: MainUseCase) : ViewModel() {
    private val _vaksinasi = MutableLiveData<Resource<List<VaksinasiData>>>()

    val vaksinasi: LiveData<Resource<List<VaksinasiData>>> get() = _vaksinasi

    private val _result = MutableLiveData<Resource<AddVaksinasiPet>>()

    val result: LiveData<Resource<AddVaksinasiPet>> get() =  _result

    private val _medical =  MutableLiveData<Resource<List<MedicalItem>>>()

    val medical: LiveData<Resource<List<MedicalItem>>> get() = _medical

    private val _resultInsert = MutableLiveData<Resource<MedicalRecord>> ()

    val resultInsert: LiveData<Resource<MedicalRecord>> get() =  _resultInsert

    fun getVaksinasi(token: String, userId: Int) {
        _vaksinasi.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getVaksinasi(token, userId).collect {
                _vaksinasi.value = it
            }
        }
    }

    fun insertVaksinasi(token: String, data: VaksinasiItem) {
        _result.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.insertVaksinasi(token, data).collect {
                _result.value = it
            }
        }
    }

    fun getMedicalRecord(token: String, userId: Int) {
        _medical.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getMedicalRecord(token, userId).collect {
                _medical.value = it
            }
        }
    }

    fun insertMedicalRecord(token: String, data: DataItem) {
        _resultInsert.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.insertMedicalRecord(token, data).collect {
                _resultInsert.value = it
            }
        }
    }
}