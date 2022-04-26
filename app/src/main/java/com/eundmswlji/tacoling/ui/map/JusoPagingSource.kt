package com.eundmswlji.tacoling.ui.map

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eundmswlji.tacoling.data.model.Juso
import com.eundmswlji.tacoling.retrofit.services.JusoService

class JusoPagingSource(
    private val service : JusoService,
    private val query : String
) : PagingSource<Int,Juso>() {
    override fun getRefreshKey(state: PagingState<Int, Juso>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Juso> {
        val key = params.key ?: 1
        return try {
           val response = service.GetJuso(query,key,10)
        }
    }
}