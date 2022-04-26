package com.eundmswlji.tacoling.ui.map

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.repository.JusoRepository
import retrofit2.HttpException
import java.io.IOException

class JusoPagingSource(
    private val jusoRepository: JusoRepository,
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
            val response = jusoRepository.apiGetJuso(query, key, 10).body()!!.documents
            return PagingSource.LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isNullOrEmpty()) null else key.plus(1)
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}