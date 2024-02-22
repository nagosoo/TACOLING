package com.eundmswlji.tacoling.data.retrofit.services

import com.eundmswlji.tacoling.data.model.Shop
import com.eundmswlji.tacoling.data.model.Shops
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopService {
    @GET("/shop.json")
    suspend fun getAllShops(
        @Query("orderBy") orderBy: String = "\"zero_waste\"",
    ): Response<Shops?>

    @GET("/shop/item/{shopId}.json")
    suspend fun getShopInfo(
        @Path("shopId") shopId: Int
    ): Response<Shop?>
}