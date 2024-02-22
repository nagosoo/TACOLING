package com.eundmswlji.tacoling.domain.model

data class UserInfoModel(
    val favoriteShops: List<FavoriteShopModel>,
    val notification: Boolean = true
)