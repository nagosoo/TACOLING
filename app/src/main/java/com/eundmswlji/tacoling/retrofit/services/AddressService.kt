package com.eundmswlji.tacoling.retrofit.services

import com.eundmswlji.tacoling.data.model.Juso
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressService {
    @GET("/v2/local/search/address.json")
   suspend fun apiGetAddress(
        @Query ("query") query : String,
        @Query ("page") page : Int,
        @Query ("size") size : Int
    ) : Response<Juso>
}
