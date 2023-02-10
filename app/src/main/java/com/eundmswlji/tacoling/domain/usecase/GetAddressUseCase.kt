package com.eundmswlji.tacoling.domain.usecase

import com.eundmswlji.tacoling.data.repository.address.AddressRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(query: String) = addressRepository.getAddress(query)
}

