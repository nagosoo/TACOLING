package com.eundmswlji.tacoling.data.source.local.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eundmswlji.tacoling.data.source.local.user.UserLocalDataSourceImpl.PreferenceKeys.USER_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserLocalDataSource {

    private object PreferenceKeys {
        val USER_ID = stringPreferencesKey("userId")
    }

    override suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID] = userId
        }
    }

    override suspend fun getUserId(): Flow<String?> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                prefs[USER_ID]
            }

}