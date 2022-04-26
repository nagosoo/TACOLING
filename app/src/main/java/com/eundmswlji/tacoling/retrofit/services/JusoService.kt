package com.eundmswlji.tacoling.retrofit.services

import com.eundmswlji.tacoling.data.model.Juso
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface JusoService {
    @GET("/v2/local/search/address.json")
   suspend fun apiGetJuso(
        @Query ("query") query : String
    ) : Response<Juso>

    @GET("/v2/local/search/address.json")
    suspend fun GetJuso(
        @Query ("query") query : String,
        @Query ("page") page : Int,
        @Query ("size") size : Int
    ) : Response<Juso>
}
