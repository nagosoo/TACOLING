package com.eundmswlji.tacoling.data.source.local.user

import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {

    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): Flow<String?>
    suspend fun clearUserId()
}