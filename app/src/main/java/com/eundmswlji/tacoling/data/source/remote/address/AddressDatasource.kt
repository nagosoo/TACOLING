package com.eundmswlji.tacoling.data.source.remote.address

import com.eundmswlji.tacoling.data.model.AddressFromKakao
import retrofit2.Response

interface AddressDatasource {
    suspend fun getAddress(query: String, size: Int, page: Int): Response<AddressFromKakao>
}