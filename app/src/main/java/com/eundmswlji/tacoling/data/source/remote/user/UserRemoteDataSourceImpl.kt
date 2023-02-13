package com.eundmswlji.tacoling.data.source.remote.user

import com.eundmswlji.tacoling.data.model.*
import com.eundmswlji.tacoling.retrofit.services.UserService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService
) : UserRemoteDataSource {
    override suspend fun postUser(body: UserInfo): Response<UserResponse> = userService.postUser(body)
    override suspend fun deleteUser(userId: String): Response<UserResponse> = userService.deleteUser(userId)
    override suspend fun getUserInfo(userId: String): Response<UserInfo> = userService.getUserInfo(userId)
    override suspend fun getUserLikedShops(userId: String): Response<List<LikedShop>> =
        userService.getUserLikedShops(userId)

    override suspend fun addLikedShop(userId: String, shopId: Int): Response<AddLikedShopResponse> =
        userService.addLikedShop(userId, shopId)

    override suspend fun deleteLikedShop(userId: String, shopId: Int): Response<Nothing> =
        userService.deleteLikedShop(userId, shopId)

    override suspend fun patchAlarm(userId: String, body: Alarm): Response<Alarm> =
        userService.patchAlarm(userId, body)

}