package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserKeyUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.clearUserKey()
}