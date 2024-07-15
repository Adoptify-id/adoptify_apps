package com.example.adoptify_core.ui.bookmark

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.data.source.local.entity.PetEntity
import com.example.core.domain.usecase.MainUseCase
import kotlinx.coroutines.launch

class BookmarkViewModel(private val mainUseCase: MainUseCase) : ViewModel() {
    private val _favorite = MutableLiveData<Resource<List<PetEntity>>>()

    val favorite: LiveData<Resource<List<PetEntity>>> get() = _favorite

    fun getFavoritePet() {
        _favorite.value = Resource.Loading(true)
        viewModelScope.launch {
            mainUseCase.getFavoritePets().collect {
                _favorite.value = it
            }
        }
    }

    fun insertToFavorite(petsEntity: PetEntity, isFavorite: Boolean) {
        viewModelScope.launch {
            mainUseCase.insertToFavorite(petsEntity, isFavorite).collect {
                Log.d("MainViewModel", "data: $it")
            }
        }
    }

    fun deleteFromFavorite(petsEntity: PetEntity, isFavorite: Boolean) {
        viewModelScope.launch {
            mainUseCase.deleteFromFavorite(petsEntity, isFavorite).collect {
                getFavoritePet()
            }
        }
    }

    fun isFavorite(petId: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            mainUseCase.getFavoritePets().collect { resource ->
                if (resource is Resource.Success) {
                    val isFavorite = resource.data?.any { it.id == petId } == true
                    result.postValue(isFavorite)
                } else {
                    result.postValue(false)
                }
            }
        }
        return result
    }
}