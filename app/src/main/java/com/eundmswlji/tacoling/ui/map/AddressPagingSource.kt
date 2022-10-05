package com.eundmswlji.tacoling.ui.map

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.repository.AddressRepository
import retrofit2.HttpException
import java.io.IOException

class AddressPagingSource(
    private val addressRepository: AddressRepository,
    private val query: String
) : PagingSource<Int, Document>() {
    override fun getRefreshKey(state: PagingState<Int, Document>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): PagingSource.LoadResult<Int, Document> {
        val key = params.key ?: 1
        return try {
            val response = addressRepository.apiGetAddress(query, key, 10)
            val document = response.body()?.documents!!
            val isEnd = response.body()?.meta?.isEnd
            PagingSource.LoadResult.Page(
                data = document,
                prevKey = null,
                nextKey = if (isEnd==true) null else key.plus(1)
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}