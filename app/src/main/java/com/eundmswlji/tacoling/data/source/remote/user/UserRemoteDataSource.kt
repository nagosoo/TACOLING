package com.eundmswlji.tacoling.data.source.remote.user

import com.eundmswlji.tacoling.data.model.*
import com.eundmswlji.tacoling.data.retrofit.services.UserService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val userService: UserService
)  {
     suspend fun signUp(body: UserInfo): Response<UserKey?> =
        userService.signUp(body)

     suspend fun deleteUser(userId: String): Response<ResponseBody?> =
        userService.deleteUser(userId)

     suspend fun getUserInfo(userId: String): Response<UserInfo?> =
        userService.getUserInfo(userId)

     suspend fun getFavoriteShops(userId: String): Response<List<FavoriteShop>?> =
        userService.getFavoriteShops(userId)

     suspend fun addFavoriteShop(
        userId: String,
        shopIndex: Int,
        body: FavoriteShop
    ): Response<PatchFavoriteShopRes?> =
        userService.addFavoriteShop(userId, shopIndex, body)

     suspend fun deleteFavoriteShop(userId: String, shopIndex: Int): Response<ResponseBody?> =
        userService.deleteFavoriteShop(userId, shopIndex)

     suspend fun patchAlarm(userId: String, body: Alarm): Response<Alarm?> =
        userService.patchAlarm(userId, body)
}