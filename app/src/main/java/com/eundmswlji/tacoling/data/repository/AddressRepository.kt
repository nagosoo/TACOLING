package com.eundmswlji.tacoling.data.repository

import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.model.Juso
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AddressRepository {
    suspend fun getAddress(query: String) : Flow<PagingData<Document>>
    suspend fun apiGetAddress(query: String, size: Int, page: Int) : Response<Juso>
}