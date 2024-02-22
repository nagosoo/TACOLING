package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.data.model.UserInfo
import com.eundmswlji.tacoling.domain.repository.UserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userInfo: UserInfo) = userRepository.signUp(userInfo)
}