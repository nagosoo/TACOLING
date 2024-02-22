package com.eundmswlji.tacoling.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.data.model.Alarm
import com.eundmswlji.tacoling.domain.model.AlarmModel
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.domain.usecase.user.DeleteUserKeyUseCase
import com.eundmswlji.tacoling.domain.usecase.user.GetUserAlarmInfoUseCase
import com.eundmswlji.tacoling.domain.usecase.user.PatchAlarmUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getUserAlarmInfoUseCase: GetUserAlarmInfoUseCase,
    private val patchAlarmUserCase: PatchAlarmUserCase,
    private val deleteUserKeyUseCase: DeleteUserKeyUseCase,

    ) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<AlarmModel?>> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<AlarmModel?>> = _uiState

    fun getAlarmInfo(userKey: String) {
        viewModelScope.launch {
            getUserAlarmInfoUseCase(userKey).collect { uiState ->
                _uiState.value = uiState
            }
        }
    }

    fun patchAlarm(userKey: String, receiveAlarm: Boolean) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            patchAlarmUserCase(userKey, Alarm(receiveAlarm)).collect { uiState ->
                _uiState.value = uiState
            }
        }
    }

    fun deleteUser(
        onDeleteSuccessCallback: () -> Unit,
    ) {
        viewModelScope.launch {
            deleteUserKeyUseCase()
        }.invokeOnCompletion { throwable ->
            if (throwable == null) onDeleteSuccessCallback()
        }
    }
}