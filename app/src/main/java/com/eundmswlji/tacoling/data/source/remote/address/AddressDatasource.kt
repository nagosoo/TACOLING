package com.eundmswlji.tacoling.data.source.remote.address

import com.eundmswlji.tacoling.data.model.Juso
import retrofit2.Response

interface AddressDatasource {
    suspend fun apiGetAddress(query: String, size: Int, page: Int): Response<Juso>
}