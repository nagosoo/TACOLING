package com.eundmswlji.tacoling.domain.repository

import com.eundmswlji.tacoling.domain.model.ShopModel
import com.eundmswlji.tacoling.domain.model.ShopsModel
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow

interface ShopRepository {

    suspend fun getShops(
    ): Flow<UiState<ShopsModel?>>

    suspend fun getShopInfo(
        shopId: Int
    ): Flow<UiState<ShopModel?>>

}