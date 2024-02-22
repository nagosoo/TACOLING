package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.data.model.Alarm
import com.eundmswlji.tacoling.domain.repository.UserRepository
import javax.inject.Inject

class PatchAlarmUserCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userKey: String,
        alarm: Alarm
    ) = userRepository.patchAlarm(userKey, alarm)
}