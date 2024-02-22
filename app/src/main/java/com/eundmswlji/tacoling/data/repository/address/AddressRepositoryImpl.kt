package com.eundmswlji.tacoling.data.repository.address

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.eundmswlji.tacoling.data.repository.address.AddressPagingSource.Companion.PAGING_SIZE
import com.eundmswlji.tacoling.data.source.remote.address.AddressDataSource
import com.eundmswlji.tacoling.domain.model.AddressModel
import com.eundmswlji.tacoling.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val addressDatasource: AddressDataSource
) : AddressRepository {
    override suspend fun getAddress(query: String): Flow<PagingData<AddressModel>> {

        val pagingSourceFactory = { AddressPagingSource(addressDatasource, query) }

        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map {
            it.map { document -> document.toDomainModel() }
        }
    }
}