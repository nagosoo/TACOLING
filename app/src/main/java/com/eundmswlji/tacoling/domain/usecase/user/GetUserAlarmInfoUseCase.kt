package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.domain.model.AlarmModel
import com.eundmswlji.tacoling.domain.repository.UserRepository
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserAlarmInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userKey: String): Flow<UiState<AlarmModel?>> {
        val userInfo = userRepository.getUserInfo(userKey)
        return userInfo.map { uiState ->
            when(uiState) {
                is UiState.Success -> {
                    val alarm = AlarmModel(uiState.value?.notification ?: true)
                    UiState.Success(alarm)
                }
                is UiState.Loading -> uiState
                is UiState.Error -> uiState
                is UiState.None -> uiState
            }
        }
    }
}