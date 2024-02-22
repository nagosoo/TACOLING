package com.eundmswlji.tacoling.data.source.remote.shop

import com.eundmswlji.tacoling.data.model.Shop
import com.eundmswlji.tacoling.data.model.Shops
import com.eundmswlji.tacoling.data.retrofit.services.ShopService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopDataSource @Inject constructor(
    private val shopService: ShopService
)  {
     suspend fun getAllShops(): Response<Shops?> =
        shopService.getAllShops()

     suspend fun getShopInfo(shopId: Int): Response<Shop?> =
        shopService.getShopInfo(shopId)
}