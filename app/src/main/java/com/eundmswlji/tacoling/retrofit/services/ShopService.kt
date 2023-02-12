package com.eundmswlji.tacoling.retrofit.services

import com.eundmswlji.tacoling.data.model.ShopX
import com.eundmswlji.tacoling.data.model.ShopXX
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopService {
    @GET("/shop.json")
    suspend fun getAllShopList(
        @Query ("orderBy") orderBy : String = "zero_waste",
        @Query ("startAt") startAt : Boolean? = null
    ) : Response<ShopX>

    @GET("/shop/{shopId}.json")
    suspend fun getShopInfo(
        @Path ("shopId") shopId : Int
    ) : Response<ShopXX>
}