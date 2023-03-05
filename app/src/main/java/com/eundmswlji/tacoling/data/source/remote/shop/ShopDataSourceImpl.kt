package com.eundmswlji.tacoling.data.source.remote.shop

import com.eundmswlji.tacoling.data.model.ShopItem
import com.eundmswlji.tacoling.data.model.ShopX
import com.eundmswlji.tacoling.retrofit.services.ShopService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopDataSourceImpl @Inject constructor(
    private val shopService: ShopService
) : ShopDataSource {
    override suspend fun getAllShopList(): Response<ShopX> =
        shopService.getAllShopList()

    override suspend fun getShopInfo(shopId: Int): Response<ShopItem> =
        shopService.getShopInfo(shopId)
}