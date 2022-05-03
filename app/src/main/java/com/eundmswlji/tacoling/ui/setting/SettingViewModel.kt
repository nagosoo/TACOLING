package com.eundmswlji.tacoling.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eundmswlji.tacoling.Event
import dagger.hilt.android.lifecycle.HiltViewModel

class SettingViewModel : ViewModel() {
    private val _isAlarmOn = MutableLiveData<Event<Boolean>>().apply { value=Event( true )}
    val isAlarmOn: LiveData<Event<Boolean>> = _isAlarmOn

    fun setAlarmOnOff(isAlarmOn: Boolean) {
        _isAlarmOn.value = Event(isAlarmOn)
    }
}