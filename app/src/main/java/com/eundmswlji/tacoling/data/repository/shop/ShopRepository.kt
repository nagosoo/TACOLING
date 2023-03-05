package com.eundmswlji.tacoling.data.repository.shop

import com.eundmswlji.tacoling.data.model.ShopItem
import retrofit2.Response

interface ShopRepository {

    suspend fun getShopList(
        zeroWaste: Boolean? = null
    ): List<ShopItem>

    suspend fun getShopInfo(
        shopId: Int
    ): Response<ShopItem>

}