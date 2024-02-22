package com.eundmswlji.tacoling.domain.usecase.shop

import com.eundmswlji.tacoling.data.model.Shops
import com.eundmswlji.tacoling.domain.model.ShopsModel
import com.eundmswlji.tacoling.domain.repository.ShopRepository
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllShopsUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(showOnlyZeroWaste: Boolean): Flow<UiState<ShopsModel?>> {
        val shops = shopRepository.getShops()
        if (!showOnlyZeroWaste) return shops
        return shops.map { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    val zeroWasteShops = ShopsModel(
                        shops = uiState.value?.shops?.filter { shop ->
                            shop.doZeroWaste
                        }
                    )
                    UiState.Success(zeroWasteShops)
                }
                is UiState.Loading -> uiState
                is UiState.Error -> uiState
                is UiState.None -> uiState
            }
        }
    }
}