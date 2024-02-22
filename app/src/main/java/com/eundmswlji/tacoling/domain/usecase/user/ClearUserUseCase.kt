package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.domain.repository.UserRepository
import javax.inject.Inject

class ClearUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String) = userRepository.deleteUser(userId)
}