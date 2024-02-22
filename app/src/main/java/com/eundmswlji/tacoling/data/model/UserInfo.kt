package com.eundmswlji.tacoling.data.model


import com.eundmswlji.tacoling.domain.model.UserInfoModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @SerialName("liked_shops")
    val favoriteShops: List<FavoriteShop>,
    val notification: Boolean = true
) : DataModelInterface<UserInfoModel> {
    override fun toDomainModel(): UserInfoModel {
        return UserInfoModel(
            favoriteShops = favoriteShops.map { it.toDomainModel() },
            notification = notification
        )
    }
}