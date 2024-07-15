package com.example.adoptify_core.ui.foster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.FormItem
import com.example.core.data.source.remote.response.PetAdoptItem
import com.example.core.domain.model.DataSubmissionFoster
import com.example.core.domain.model.DetailSubmissionFoster
import com.example.core.domain.model.FormDetail
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.usecase.MainUseCase
import kotlinx.coroutines.launch

class  FosterViewModel(private val mainUseCase: MainUseCase) : ViewModel() {
    private val _result = MutableLiveData<Resource<PetAdopt>>()

    val result: LiveData<Resource<PetAdopt>> get() = _result

    private val _detail = MutableLiveData<Resource<PetAdopt>>()

    val detail: LiveData<Resource<PetAdopt>> get() = _detail

    private val _listSubmission = MutableLiveData<Resource<List<DataSubmissionFoster>>>()

    val listSubmission: LiveData<Resource<List<DataSubmissionFoster>>> get() = _listSubmission

    private val _detailSubmission = MutableLiveData<Resource<DetailSubmissionFoster>>()

    val detailSubmission: LiveData<Resource<DetailSubmissionFoster>> get() = _detailSubmission

    private val _update = MutableLiveData<Resource<PetAdopt>>()

    val update: LiveData<Resource<PetAdopt>> get() = _update

    private val _updateStatus = MutableLiveData<Resource<FormDetail>>()

    val updateStatus: LiveData<Resource<FormDetail>> get() =  _updateStatus

    private val _updatePayment = MutableLiveData<Resource<FormDetail>>()

    val updatePayment: LiveData<Resource<FormDetail>> get() = _updatePayment

    private val _updatePickup = MutableLiveData<Resource<FormDetail>>()

    val updatePickup: LiveData<Resource<FormDetail>> get() =  _updatePickup

    private val _formDetail = MutableLiveData<Resource<FormDetail>>()

    val formDetail: LiveData<Resource<FormDetail>> get() = _formDetail

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

    fun getListSubmissionFoster(token: String, userId: Int) {
        _listSubmission.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getListSubmissionFoster(token, userId).collect {
                _listSubmission.value = it
            }
        }
    }

    fun detailSubmissionFoster(token: String, reqId: Int) {
        _detailSubmission.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.detailSubmissionFoster(token, reqId).collect {
                _detailSubmission.value = it
            }
        }
    }

    fun updateAdopt(token: String, petId: Int, isAdopt: Boolean) {
        _update.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.updateAdopt(token, petId, isAdopt).collect {
                _update.value = it
            }
        }
    }

    fun updateStatusReq(token: String, reqId: Int, statusReq: Int) {
        _updateStatus.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.updateStatusReq(token, reqId, statusReq).collect {
                _updateStatus.value = it
            }
        }
    }

    fun updateStatusPayment(token: String, reqId: Int, statusPayment: Int) {
        _updatePayment.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.updateStatusPaymentFoster(token, reqId, statusPayment).collect {
                _updatePayment.value = it
            }
        }
    }

    fun updateStatusPickup(token: String, reqId: Int, data: FormItem) {
        _updatePickup.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.updateStatusPickup(token, reqId, data).collect {
                _updatePickup.value = it
            }
        }
    }

    fun getFormDetail(token: String, reqId: Int) {
        _formDetail.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getFormDetail(token, reqId).collect {
                _formDetail.value = it
            }
        }
    }
}