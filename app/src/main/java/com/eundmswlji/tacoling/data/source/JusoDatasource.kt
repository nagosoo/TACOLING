package com.eundmswlji.tacoling.data.source

import com.eundmswlji.tacoling.data.model.Juso
import retrofit2.Response

interface JusoDatasource {
    suspend fun apiGetJuso(query: String, size: Int, page: Int): Response<Juso>
}