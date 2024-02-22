package com.eundmswlji.tacoling.data.model


import com.eundmswlji.tacoling.domain.model.FavoriteShopModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteShop(
    @SerialName("id")
    val shopId: Int,
    @SerialName("name")
    val shopName: String
) : DataModelInterface<FavoriteShopModel> {
    override fun toDomainModel(): FavoriteShopModel {
        return FavoriteShopModel(shopId, shopName)
    }
}