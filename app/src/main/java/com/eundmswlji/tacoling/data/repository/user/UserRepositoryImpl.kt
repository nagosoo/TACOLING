package com.eundmswlji.tacoling.data.repository.user

import com.eundmswlji.tacoling.data.model.*
import com.eundmswlji.tacoling.data.source.local.user.UserLocalDataSource
import com.eundmswlji.tacoling.data.source.remote.user.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : UserRepository {
    override suspend fun postUser(body: UserInfo): Response<UserResponse> =
        userRemoteDataSource.postUser(body)

    override suspend fun deleteUser(userId: String): Response<Nothing> =
        userRemoteDataSource.deleteUser(userId)

    //    override suspend fun getUserInfo(userId: Int): Response<UserInfo> =
//        userRemoteDataSource.getUserInfo(userId)
    override suspend fun getUserAlarmInfo(userId: String): Boolean {
        val response = userRemoteDataSource.getUserInfo(userId)
        return if (response.isSuccessful) response.body()?.notification ?: true
        else true
    }

    override suspend fun getUserLikedShops(userId: String): Response<List<LikedShop>> =
        userRemoteDataSource.getUserLikedShops(userId)

    override suspend fun addLikedShop(userId: String, shopId: Int): Response<AddLikedShopResponse> =
        userRemoteDataSource.addLikedShop(userId, shopId)

    override suspend fun deleteLikedShop(userId: String, shopId: Int): Response<Nothing> =
        userRemoteDataSource.deleteLikedShop(userId, shopId)

    override suspend fun patchAlarm(userId: String, body: Alarm): Response<Alarm> =
        userRemoteDataSource.patchAlarm(userId, body)

    override suspend fun saveUserId(userId: String) = userLocalDataSource.saveUserId(userId)

    override suspend fun getUserId(): Flow<String?> =
        userLocalDataSource.getUserId()

    override suspend fun clearUserId() = userLocalDataSource.clearUserId()
}