package com.eundmswlji.tacoling.data.source

import com.eundmswlji.tacoling.data.model.Juso
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response
import java.util.concurrent.Flow

interface JusoDatasource {
    suspend fun apiGetJuso(query: String): Response<Juso>
    suspend fun getJuso(query: String, size: Int, page: Int) : Response<Juso>
}