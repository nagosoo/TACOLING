package com.eundmswlji.tacoling.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.data.model.Alarm
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val isAlarmChecked = MutableLiveData<Boolean>()
    private var userId: String? = null

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

    private val _isUserDeletedSuccessful = MutableLiveData<Boolean>()
    val isUserDeletedSuccessful: LiveData<Boolean> = _isUserDeletedSuccessful

    init {
        viewModelScope.launch {
            userId = userRepository.getUserId()
        }
    }

    fun getAlarmInfo() {
        viewModelScope.launch {
            userId?.let { userId ->
                val response = userRepository.getUserAlarmInfo(userId)
                isAlarmChecked.postValue(response)
            }
        }
    }

    fun patchAlarm(isAlarmChecked: Boolean) = viewModelScope.launch {
        userId?.let { userId ->
            val response =
                userRepository.patchAlarm(userId, Alarm(notification = isAlarmChecked))
            if (!response.isSuccessful)
                _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
        }
    }

    fun deleteUser() = viewModelScope.launch {
        userId?.let { userId ->
            val response = userRepository.deleteUser(userId)
            if (response.isSuccessful) {
                userRepository.clearUserId()
                _isUserDeletedSuccessful.postValue(true)
            } else _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
        }

    }
}