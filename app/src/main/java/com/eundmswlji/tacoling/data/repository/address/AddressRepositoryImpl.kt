package com.eundmswlji.tacoling.data.repository.address

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.model.Juso
import com.eundmswlji.tacoling.data.source.remote.address.AddressDatasource
import com.eundmswlji.tacoling.ui.map.AddressPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressDatasource: AddressDatasource
) : AddressRepository {
    override suspend fun apiGetAddress(query: String, size: Int, page: Int): Response<Juso> =
        addressDatasource.apiGetAddress(query, size, page)

    override suspend fun getAddress(query: String): Flow<PagingData<Document>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = { AddressPagingSource(this, query) }
        ).flow
    }
}