package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserKeyUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userKey: String) = userRepository.saveUserKey(userKey)
}