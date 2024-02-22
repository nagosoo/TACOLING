package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.data.model.FavoriteShop
import com.eundmswlji.tacoling.domain.repository.UserRepository
import javax.inject.Inject

class AddFavoriteShopUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        userKey: String,
        favoriteShopsSize: Int,
        favoriteShop: FavoriteShop
    ) = userRepository.addFavoriteShop(userKey, favoriteShopsSize, favoriteShop)

}