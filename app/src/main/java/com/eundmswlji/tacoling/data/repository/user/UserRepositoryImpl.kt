package com.eundmswlji.tacoling.data.repository.user

import com.eundmswlji.tacoling.data.model.*
import com.eundmswlji.tacoling.data.source.remote.user.UserDataSource
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override fun postUser(body: UserInfo): Response<UserResponse> = userDataSource.postUser(body)
    override fun deleteUser(userId: Int): Response<UserResponse> = userDataSource.deleteUser(userId)
    override fun getUserInfo(userId: Int): Response<UserInfo> = userDataSource.getUserInfo(userId)
    override fun getUserLikedShops(userId: Int): Response<List<LikedShop>> =
        userDataSource.getUserLikedShops(userId)

    override fun addLikedShop(userId: Int, shopId: Int): Response<AddLikedShopResponse> =
        userDataSource.addLikedShop(userId, shopId)

    override fun deleteLikedShop(userId: Int, shopId: Int): Response<Nothing> =
        userDataSource.deleteLikedShop(userId, shopId)

    override fun patchAlarm(userId: Int, body: Alarm): Response<Alarm> =
        userDataSource.patchAlarm(userId, body)
}