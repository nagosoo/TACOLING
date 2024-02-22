package com.eundmswlji.tacoling.data.retrofit.services

import com.eundmswlji.tacoling.data.model.address.AddressPaging
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressService {
    @GET("/v2/local/search/address.json")
    suspend fun getAddress(
        @Query("query") query: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Response<AddressPaging>
}
