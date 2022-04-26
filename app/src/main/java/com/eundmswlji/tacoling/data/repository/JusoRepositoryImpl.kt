package com.eundmswlji.tacoling.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eundmswlji.tacoling.data.model.Juso
import com.eundmswlji.tacoling.data.source.JusoDatasource
import com.eundmswlji.tacoling.ui.map.JusoPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class JusoRepositoryImpl @Inject constructor(
    private val jusoDatasource: JusoDatasource
) : JusoRepository {
    override suspend fun apiGetJuso(query: String): Response<Juso> = jusoDatasource.apiGetJuso(query)
    override suspend fun GetJuso(query: String, size: Int, page: Int): Flow<PagingData<Juso>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = { JusoPagingSource() }
        ).flow
    }
}