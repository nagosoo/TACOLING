package com.eundmswlji.tacoling.data.source.remote.user

import com.eundmswlji.tacoling.data.model.*
import com.eundmswlji.tacoling.retrofit.services.UserService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(
    private val userService: UserService
) : UserDataSource {
    override suspend fun postUser(body: UserInfo): Response<UserResponse> = userService.postUser(body)
    override suspend fun deleteUser(userId: Int): Response<UserResponse> = userService.deleteUser(userId)
    override suspend fun getUserInfo(userId: Int): Response<UserInfo> = userService.getUserInfo(userId)
    override suspend fun getUserLikedShops(userId: Int): Response<List<LikedShop>> =
        userService.getUserLikedShops(userId)

    override suspend fun addLikedShop(userId: Int, shopId: Int): Response<AddLikedShopResponse> =
        userService.addLikedShop(userId, shopId)

    override suspend fun deleteLikedShop(userId: Int, shopId: Int): Response<Nothing> =
        userService.deleteLikedShop(userId, shopId)

    override suspend fun patchAlarm(userId: Int, body: Alarm): Response<Alarm> =
        userService.patchAlarm(userId, body)

}