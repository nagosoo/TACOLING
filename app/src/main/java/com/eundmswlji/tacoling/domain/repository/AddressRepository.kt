package com.eundmswlji.tacoling.domain.repository

import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.address.Document
import com.eundmswlji.tacoling.domain.model.AddressModel
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    suspend fun getAddress(query: String): Flow<PagingData<AddressModel>>
}