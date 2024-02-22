package com.eundmswlji.tacoling.data.repository.shop

import com.eundmswlji.tacoling.data.safeApiCall
import com.eundmswlji.tacoling.data.source.remote.shop.ShopDataSource
import com.eundmswlji.tacoling.data.toDomainModelFlow
import com.eundmswlji.tacoling.domain.model.ShopModel
import com.eundmswlji.tacoling.domain.model.ShopsModel
import com.eundmswlji.tacoling.domain.repository.ShopRepository
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val shopDataSource: ShopDataSource
) : ShopRepository {

    override suspend fun getShops(): Flow<UiState<ShopsModel?>> =
        safeApiCall { shopDataSource.getAllShops() }.map { uiState ->
            uiState.toDomainModelFlow()
        }

    override suspend fun getShopInfo(shopId: Int): Flow<UiState<ShopModel?>> =
        safeApiCall { shopDataSource.getShopInfo(shopId) }.map { uiState ->
            uiState.toDomainModelFlow()
        }

}