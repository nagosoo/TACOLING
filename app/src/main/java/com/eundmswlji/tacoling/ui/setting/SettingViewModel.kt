package com.eundmswlji.tacoling.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.Const.USER_ID
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

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

    private val _isUserDeletedSuccessfully = MutableLiveData<Boolean>()
    val isUserDeletedSuccessfully: LiveData<Boolean> = _isUserDeletedSuccessfully

    fun getAlarmInfo() {
        viewModelScope.launch {
            val response = userRepository.getUserAlarmInfo(USER_ID!!)
            isAlarmChecked.postValue(response)
        }
    }

    //서버에 패치 되기 전에 프래그먼트를 나가버리면?
    fun patchAlarm(isAlarmChecked: Boolean) = viewModelScope.launch {
        val response = userRepository.patchAlarm(USER_ID!!, Alarm(notification = isAlarmChecked))
        if (!response.isSuccessful)
            _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
    }

    fun deleteUser() = viewModelScope.launch {
        val response = userRepository.deleteUser(USER_ID!!)
        if (response.isSuccessful) {
            userRepository.clearUserId()
            _isUserDeletedSuccessfully.postValue(true)
        } else _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
    }
}