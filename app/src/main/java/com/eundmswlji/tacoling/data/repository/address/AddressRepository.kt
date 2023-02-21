package com.eundmswlji.tacoling.data.repository.address

import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.model.AddressFromKakao
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AddressRepository {
    suspend fun getAddress(query: String) : Flow<PagingData<Document>>
}