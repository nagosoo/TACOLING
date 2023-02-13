package com.eundmswlji.tacoling.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.data.model.Alarm
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val isAlarmChecked = MutableLiveData<Boolean>()

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

    init {
        getUserId()

    }

    private val _userId = MutableStateFlow<String?>("")
    val userId: StateFlow<String?> = _userId
    private fun getUserId() {
        viewModelScope.launch {
            _userId.value = userRepository.getUserId().first()
        }
    }
    private fun getAlarmInfo() {
        viewModelScope.launch {
            val response = userRepository.getUserAlarmInfo(userId.value!!)
            isAlarmChecked.postValue()
        }
    }

    //서버에 패치 되기 전에 프래그먼트를 나가버리면?
    fun patchAlarm(isAlarmChecked: Boolean) = viewModelScope.launch {
        userId.value?.let { userId ->
            val response = userRepository.patchAlarm(userId, Alarm(notification = isAlarmChecked))
            response.errorBody()?.let {
                _toastHelper.postValue(
                    Event(it.string())
                )
            }
        }
    }

    fun deleteUser(userId: String) = viewModelScope.launch {
        val response = userRepository.deleteUser(userId)
        if (response.isSuccessful)
    }
}