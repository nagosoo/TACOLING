package com.eundmswlji.tacoling.data.repository.address

import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Document
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    suspend fun getAddress(query: String): Flow<PagingData<Document>>
}