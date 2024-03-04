package com.idphoto.idphotomaster.core.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataStoreImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    LocalDataStore {
    companion object {
        const val DATA = "Data"
        private const val IsUserSawTutorial = "IsUserSawTutorial"
        private const val IsUserSawCameraTutorial = "IsUserSawCameraTutorial"
        private const val IsAppOpenedBefore = "IsAppOpenedBefore"
        val isUserSawTutorial = booleanPreferencesKey(IsUserSawTutorial)
        val isUserSawCameraTutorial = booleanPreferencesKey(IsUserSawCameraTutorial)
        val isAppOpenedBefore = booleanPreferencesKey(IsAppOpenedBefore)
    }

    override fun isUserSawTutorial(): Flow<Boolean> {
        return dataStore.data.catch {
            emit(emptyPreferences())
        }.map { preference ->
            preference[isUserSawTutorial] ?: false
        }
    }

    override suspend fun setUserSawTutorial(seen: Boolean) {
        dataStore.edit { preference ->
            preference[isUserSawTutorial] = seen
        }
    }

    override fun isUserSawCameraTutorial(): Flow<Boolean> {
        return dataStore.data.catch {
            emit(emptyPreferences())
        }.map { preference ->
            preference[isUserSawCameraTutorial] ?: false
        }
    }

    override suspend fun setUserSawCameraTutorial(seen: Boolean) {
        dataStore.edit { preference ->
            preference[isUserSawCameraTutorial] = seen
        }
    }

    override fun isAppOpenedBefore(): Flow<Boolean> {
        return dataStore.data.catch {
            emit(emptyPreferences())
        }.map { preference ->
            preference[isAppOpenedBefore] ?: false
        }
    }

    override suspend fun setIsAppOpenedBefore(isFirstOpen: Boolean) {
        dataStore.edit { preference ->
            preference[isAppOpenedBefore] = isFirstOpen
        }
    }
}