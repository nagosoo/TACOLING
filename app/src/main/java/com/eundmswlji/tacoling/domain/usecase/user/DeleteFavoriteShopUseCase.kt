package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.domain.repository.UserRepository
import javax.inject.Inject

class DeleteFavoriteShopUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        userKey: String,
        indexInFavorites: Int,
    ) = userRepository.deleteFavoriteShop(userKey, indexInFavorites)

}