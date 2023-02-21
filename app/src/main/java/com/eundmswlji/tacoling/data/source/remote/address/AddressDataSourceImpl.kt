package com.eundmswlji.tacoling.data.source.remote.address

import com.eundmswlji.tacoling.data.model.AddressFromKakao
import com.eundmswlji.tacoling.retrofit.services.AddressService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressDataSourceImpl @Inject constructor(
    private val addressService: AddressService
) : AddressDatasource {
    override suspend fun getAddress(
        query: String,
        size: Int,
        page: Int
    ): Response<AddressFromKakao> =
        addressService.getAddress(query, size, page)
}