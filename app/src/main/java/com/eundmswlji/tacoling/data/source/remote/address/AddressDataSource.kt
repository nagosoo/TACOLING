package com.eundmswlji.tacoling.data.source.remote.address

import com.eundmswlji.tacoling.data.model.address.AddressPaging
import com.eundmswlji.tacoling.data.retrofit.services.AddressService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressDataSource @Inject constructor(
    private val addressService: AddressService
) {
    suspend fun getAddress(
        query: String,
        size: Int,
        page: Int
    ): Response<AddressPaging> =
        addressService.getAddress(query, size, page)
}