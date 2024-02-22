package com.eundmswlji.tacoling.domain.usecase.address

import com.eundmswlji.tacoling.domain.repository.AddressRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(query: String) = addressRepository.getAddress(query)
}