package com.eundmswlji.tacoling.data.source.remote.shop

import com.eundmswlji.tacoling.data.model.ShopX
import com.eundmswlji.tacoling.data.model.ShopXX
import retrofit2.Response

interface ShopDataSource {

    suspend fun getAllShopList(
        orderBy: String = "zero_waste",
        startAt: Boolean? = null
    ): Response<ShopX>

    suspend fun getShopInfo(
        shopId: Int
    ): Response<ShopXX>

}