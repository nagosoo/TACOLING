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
    override fun postUser(body: UserInfo): Response<UserResponse> = userService.postUser(body)
    override fun deleteUser(userId: Int): Response<UserResponse> = userService.deleteUser(userId)
    override fun getUserInfo(userId: Int): Response<UserInfo> = userService.getUserInfo(userId)
    override fun getUserLikedShops(userId: Int): Response<List<LikedShop>> =
        userService.getUserLikedShops(userId)

    override fun addLikedShop(userId: Int, shopId: Int): Response<AddLikedShopResponse> =
        userService.addLikedShop(userId, shopId)

    override fun deleteLikedShop(userId: Int, shopId: Int): Response<Nothing> =
        userService.deleteLikedShop(userId, shopId)

    override fun patchAlarm(userId: Int, body: Alarm): Response<Alarm> =
        userService.patchAlarm(userId, body)

}