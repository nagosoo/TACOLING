package com.eundmswlji.tacoling.data.source

import com.eundmswlji.tacoling.data.model.Juso
import com.eundmswlji.tacoling.retrofit.services.AddressService
import retrofit2.Response
import javax.inject.Inject

class AddressDataSourceImpl @Inject constructor(
    private val addressService: AddressService
) : AddressDatasource {
    override suspend fun apiGetAddress(query: String, size: Int, page: Int): Response<Juso> =
        addressService.apiGetAddress(query, size, page)
}