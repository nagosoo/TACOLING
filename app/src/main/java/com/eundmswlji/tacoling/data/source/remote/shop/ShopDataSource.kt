package com.eundmswlji.tacoling.data.source.remote.shop

import com.eundmswlji.tacoling.data.model.ShopItem
import com.eundmswlji.tacoling.data.model.ShopX
import retrofit2.Response

interface ShopDataSource {

    suspend fun getAllShopList(): Response<ShopX>

    suspend fun getShopInfo(
        shopId: Int
    ): Response<ShopItem>

}