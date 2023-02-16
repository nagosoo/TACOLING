package com.eundmswlji.tacoling.data.source.remote.user

import com.eundmswlji.tacoling.data.model.*
import com.eundmswlji.tacoling.retrofit.services.UserService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService
) : UserRemoteDataSource {
    override suspend fun postUser(body: UserInfo): Response<UserResponse> =
        userService.postUser(body)

    override suspend fun deleteUser(userId: String): Response<ResponseBody> =
        userService.deleteUser(userId)

    override suspend fun getUserInfo(userId: String): Response<UserInfo> =
        userService.getUserInfo(userId)

    override suspend fun getUserLikedShops(userId: String): Response<List<LikedShopX>> =
        userService.getUserLikedShops(userId)

    override suspend fun addLikedShop(
        userId: String,
        shopIndex: Int,
        body: LikedShopX
    ): Response<AddLikedShopResponse> =
        userService.addLikedShop(userId, shopIndex, body)

    override suspend fun deleteLikedShop(userId: String, shopIndex: Int): Response<ResponseBody> =
        userService.deleteLikedShop(userId, shopIndex)

    override suspend fun patchAlarm(userId: String, body: Alarm): Response<Alarm> =
        userService.patchAlarm(userId, body)
}