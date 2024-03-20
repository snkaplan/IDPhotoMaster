package com.idphoto.idphotomaster.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.idphoto.idphotomaster.core.data.datasource.local.LocalDataStore
import com.idphoto.idphotomaster.core.data.datasource.local.LocalDataStoreImpl
import com.idphoto.idphotomaster.core.data.datasource.remote.BasketRemoteDataSource
import com.idphoto.idphotomaster.core.data.datasource.remote.BasketRemoteDataSourceImpl
import com.idphoto.idphotomaster.core.data.repository.BasketRepository
import com.idphoto.idphotomaster.core.data.repository.BasketRepositoryImpl
import com.idphoto.idphotomaster.core.data.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Singleton
    @Provides
    fun provideLocalDataStore(dataStore: DataStore<Preferences>): LocalDataStore {
        return LocalDataStoreImpl(dataStore)
    }

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            produceFile = { context.preferencesDataStoreFile(LocalDataStoreImpl.DATA) })
    }

    @Singleton
    @Provides
    fun provideBasketRepository(basketRemoteDataSource: BasketRemoteDataSource): BasketRepository {
        return BasketRepositoryImpl(basketRemoteDataSource)
    }

    @Singleton
    @Provides
    fun provideBasketRemoteDataSource(
        firebaseFirestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        networkMonitor: NetworkMonitor
    ): BasketRemoteDataSource {
        return BasketRemoteDataSourceImpl(firebaseFirestore, firebaseStorage, networkMonitor)
    }
}