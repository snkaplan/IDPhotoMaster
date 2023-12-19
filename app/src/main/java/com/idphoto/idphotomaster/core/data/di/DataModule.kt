package com.idphoto.idphotomaster.core.data.di

import com.idphoto.idphotomaster.core.data.util.ConnectivityManager
import com.idphoto.idphotomaster.core.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManager,
    ): NetworkMonitor
}