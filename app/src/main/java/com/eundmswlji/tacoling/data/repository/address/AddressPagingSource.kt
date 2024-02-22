package com.eundmswlji.tacoling.data.repository.address

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eundmswlji.tacoling.data.model.address.Document
import com.eundmswlji.tacoling.data.source.remote.address.AddressDataSource
import retrofit2.HttpException
import java.io.IOException

class AddressPagingSource(
    private val addressDatasource: AddressDataSource,
    private val query: String
) : PagingSource<Int, Document>() {
    override fun getRefreshKey(state: PagingState<Int, Document>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Document> {
        val pageNumber = params.key ?: 1
        return try {
            val response = addressDatasource.getAddress(query, PAGING_SIZE, pageNumber)
            val data = response.body()?.documents!!
            val isEnd = response.body()?.meta?.isEnd!!
            LoadResult.Page(
                data = data,
                prevKey = if (pageNumber == 1) null else pageNumber - 1,
                nextKey = if (isEnd) null else pageNumber + (params.loadSize / PAGING_SIZE)
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        const val PAGING_SIZE = 10
    }
}