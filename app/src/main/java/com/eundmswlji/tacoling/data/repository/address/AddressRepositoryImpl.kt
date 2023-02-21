package com.eundmswlji.tacoling.data.repository.address

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.repository.address.AddressPagingSource.Companion.PAGING_SIZE
import com.eundmswlji.tacoling.data.source.remote.address.AddressDatasource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val addressDatasource: AddressDatasource
) : AddressRepository {
    override suspend fun getAddress(query: String): Flow<PagingData<Document>> {

        val pagingSourceFactory = { AddressPagingSource(addressDatasource, query) }

        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}