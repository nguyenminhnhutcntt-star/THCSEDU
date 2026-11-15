package com.example.thcsedu.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "thcsedu_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val ROLE_KEY = stringPreferencesKey("user_role")
        val USERNAME_KEY = stringPreferencesKey("user_username")
        val MA_DINH_DANH_KEY = stringPreferencesKey("user_ma_dinh_danh")
        val HO_TEN_KEY = stringPreferencesKey("user_ho_ten")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.TOKEN_KEY] }

    val roleFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.ROLE_KEY] }

    val usernameFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.USERNAME_KEY] }

    val maDinhDanhFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.MA_DINH_DANH_KEY] }

    val hoTenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.HO_TEN_KEY] }

    suspend fun saveAuthData(
        token: String,
        role: String,
        username: String = "",
        maDinhDanh: String? = null,
        hoTen: String = ""
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN_KEY] = token
            preferences[PreferencesKeys.ROLE_KEY] = role
            preferences[PreferencesKeys.USERNAME_KEY] = username
            if (maDinhDanh != null) {
                preferences[PreferencesKeys.MA_DINH_DANH_KEY] = maDinhDanh
            } else {
                preferences.remove(PreferencesKeys.MA_DINH_DANH_KEY)
            }
            preferences[PreferencesKeys.HO_TEN_KEY] = hoTen
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
