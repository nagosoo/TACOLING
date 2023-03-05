package com.eundmswlji.tacoling.data.repository.shop

import com.eundmswlji.tacoling.data.model.ShopItem
import com.eundmswlji.tacoling.data.source.remote.shop.ShopDataSource
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val shopDataSource: ShopDataSource
) : ShopRepository {

    override suspend fun getShopList(zeroWaste: Boolean?): List<ShopItem> {
        val response = shopDataSource.getAllShopList()
        if (response.isSuccessful) {
            val shopList = response.body()!!.item
            return if (zeroWaste == null) {
                shopList
            } else if (zeroWaste) {
                shopList.filter { it.zeroWaste }
            } else {
                shopList.filter { !it.zeroWaste }
            }
        }
        return emptyList()
    }

    override suspend fun getShopInfo(
        shopId: Int
    ): Response<ShopItem> = shopDataSource.getShopInfo(shopId)

}