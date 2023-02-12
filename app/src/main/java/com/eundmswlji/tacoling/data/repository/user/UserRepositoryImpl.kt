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
    override suspend fun postUser(body: UserInfo): Response<UserResponse> =
        userDataSource.postUser(body)

    override suspend fun deleteUser(userId: Int): Response<UserResponse> =
        userDataSource.deleteUser(userId)

    override suspend fun getUserInfo(userId: Int): Response<UserInfo> =
        userDataSource.getUserInfo(userId)

    override suspend fun getUserLikedShops(userId: Int): Response<List<LikedShop>> =
        userDataSource.getUserLikedShops(userId)

    override suspend fun addLikedShop(userId: Int, shopId: Int): Response<AddLikedShopResponse> =
        userDataSource.addLikedShop(userId, shopId)

    override suspend fun deleteLikedShop(userId: Int, shopId: Int): Response<Nothing> =
        userDataSource.deleteLikedShop(userId, shopId)

    override suspend fun patchAlarm(userId: Int, body: Alarm): Response<Alarm> =
        userDataSource.patchAlarm(userId, body)
}