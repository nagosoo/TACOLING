package com.eundmswlji.tacoling.data.repository

import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Juso
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import retrofit2.Response

interface JusoRepository {
    suspend fun apiGetJuso(query:String) : Response<Juso>
    suspend fun GetJuso(query: String, size: Int, page: Int) : Flow<PagingData<Juso>>
}