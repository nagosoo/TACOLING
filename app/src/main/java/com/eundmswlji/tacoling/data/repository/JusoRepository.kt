package com.eundmswlji.tacoling.data.repository

import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.model.Juso
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface JusoRepository {
    suspend fun getJuso(query: String) : Flow<PagingData<Document>>
    suspend fun apiGetJuso(query: String, size: Int, page: Int) : Response<Juso>
}