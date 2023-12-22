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
        val isUserSawTutorial = booleanPreferencesKey(IsUserSawTutorial)
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
}