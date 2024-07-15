package com.example.adoptify_core.ui.adopt


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.data.source.remote.response.FormItem
import com.example.core.domain.model.DataAdopt
import com.example.core.domain.model.DetailSubmission
import com.example.core.domain.model.FormDetail
import com.example.core.domain.model.PetAdopt
import com.example.core.domain.model.SubmissionItem
import com.example.core.domain.usecase.MainUseCase
import kotlinx.coroutines.launch

class AdoptViewModel(private val mainUseCase: MainUseCase): ViewModel() {
    private val _data = MutableLiveData<Resource<List<DataAdopt>>>()

    val data: LiveData<Resource<List<DataAdopt>>> get() = _data

    private val _detail = MutableLiveData<Resource<PetAdopt>>()

    val detail: LiveData<Resource<PetAdopt>> get() = _detail

    private val _update = MutableLiveData<Resource<PetAdopt>>()

    val update: LiveData<Resource<PetAdopt>> get() = _update

    private val _form = MutableLiveData<Resource<FormDetail>>()

    val form: LiveData<Resource<FormDetail>> get() = _form

    private val _listSubmission = MutableLiveData<Resource<List<SubmissionItem>>>()

    val listSubmission: LiveData<Resource<List<SubmissionItem>>> get() = _listSubmission

    private val _detailSubmission = MutableLiveData<Resource<DetailSubmission>>()

    val detailSubmission: LiveData<Resource<DetailSubmission>> get() = _detailSubmission

    private val _updatePayment = MutableLiveData<Resource<FormDetail>>()

    val updatePayment: LiveData<Resource<FormDetail>> get() = _updatePayment

    private val _cancelAdopt = MutableLiveData<Resource<FormDetail>>()

    val cancelAdopt: LiveData<Resource<FormDetail>> get() = _cancelAdopt

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

    fun formAdopt(token: String, data: FormItem) {
        _form.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.formAdopt(token, data).collect {
                _form.value = it
            }
        }
    }

    fun getListSubmissionPet(token: String, userId: Int) {
        _listSubmission.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getListSubmissionPet(token, userId).collect {
                _listSubmission.value = it
            }
        }
    }

    fun getDetailSubmissionPet(token: String, reqId: Int) {
        _detailSubmission.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getDetailSubmissionPet(token, reqId).collect {
                _detailSubmission.value = it
            }
        }
    }

    fun updatePayment(token: String, reqId: Int, data: FormItem) {
        _updatePayment.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.updatePaymentAdopt(token, reqId, data).collect {
                _updatePayment.value = it
            }
        }
    }

    fun cancelAdopt(token: String, reqId: Int, statusReqId: Int) {
        _cancelAdopt.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.cancelAdoptUser(token, reqId, statusReqId).collect {
                _cancelAdopt.value = it
            }
        }
    }
}