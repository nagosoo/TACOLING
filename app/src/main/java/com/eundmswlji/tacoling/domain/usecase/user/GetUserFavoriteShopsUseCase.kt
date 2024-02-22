package com.eundmswlji.tacoling.domain.usecase.user

import com.eundmswlji.tacoling.data.model.FavoriteShop
import com.eundmswlji.tacoling.domain.model.FavoriteShopModel
import com.eundmswlji.tacoling.domain.repository.UserRepository
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserFavoriteShopsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userKey: String): Flow<UiState<List<FavoriteShopModel>?>> {
        val userInfo = userRepository.getUserInfo(userKey)
        return userInfo.map { uiState ->
            if (uiState is UiState.Success) {
                UiState.Success(uiState.value?.favoriteShops)
            }
            uiState as? UiState.Error ?: UiState.Error("오류가 발생 했습니다. 다시 시도해주세요.")
        }
    }
}