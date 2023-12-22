package com.idphoto.idphotomaster.core.data.di

import com.idphoto.idphotomaster.core.data.datasource.remote.UserRemoteDataSource
import com.idphoto.idphotomaster.core.data.datasource.remote.UserRemoteDataSourceImpl
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UserModule {
    @Binds
    fun bindLoginRemoteDataSource(sourceImpl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    fun bindAuthRepository(repositoryImpl: UserRepositoryImpl): UserRepository
}
