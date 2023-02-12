package com.eundmswlji.tacoling.data.repository.shop

import com.eundmswlji.tacoling.data.model.ShopX
import com.eundmswlji.tacoling.data.model.ShopXX
import com.eundmswlji.tacoling.data.source.remote.shop.ShopDataSource
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val shopDataSource: ShopDataSource
) : ShopRepository {

    override suspend fun getAllShopList(
        orderBy: String,
        startAt: Boolean?
    ): Response<ShopX> = shopDataSource.getAllShopList(orderBy, startAt)

    override suspend fun getShopInfo(
        shopId: Int
    ): Response<ShopXX> = shopDataSource.getShopInfo(shopId)

}