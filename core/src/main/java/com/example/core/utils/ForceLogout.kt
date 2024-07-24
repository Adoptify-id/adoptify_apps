package com.example.core.utils

import androidx.lifecycle.MutableLiveData

object ForceLogout  {
    val sessionExpired = MutableLiveData<Boolean>()

    fun notifySessionExpired() {
        sessionExpired.postValue(true)
    }

    fun resetSessionExpired() {
        sessionExpired.postValue(false)
    }
}