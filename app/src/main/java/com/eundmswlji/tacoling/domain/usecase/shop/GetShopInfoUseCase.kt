package com.eundmswlji.tacoling.domain.usecase.shop

import com.eundmswlji.tacoling.domain.repository.ShopRepository
import javax.inject.Inject

class GetShopInfoUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shopId: Int) = shopRepository.getShopInfo(shopId)
}