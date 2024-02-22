package com.eundmswlji.tacoling.data.source.local.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eundmswlji.tacoling.data.source.local.user.UserLocalDataSource.PreferenceKeys.USER_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferenceKeys {
        val USER_KEY = stringPreferencesKey("user_key")
    }

    suspend fun safeUserKey(userKdy: String) {
        dataStore.edit { prefs ->
            prefs[USER_KEY] = userKdy
        }
    }

    fun getUserKey(): Flow<String?> =
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
                prefs[USER_KEY]
            }

    suspend fun clearUserKey() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

}