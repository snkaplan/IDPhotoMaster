package com.idphoto.idphotomaster.core.data.di

import com.idphoto.idphotomaster.core.data.datasource.remote.BasketRemoteDataSource
import com.idphoto.idphotomaster.core.data.datasource.remote.BasketRemoteDataSourceImpl
import com.idphoto.idphotomaster.core.data.datasource.remote.OrdersRemoteDataSource
import com.idphoto.idphotomaster.core.data.datasource.remote.OrdersRemoteDataSourceImpl
import com.idphoto.idphotomaster.core.data.datasource.remote.UserRemoteDataSource
import com.idphoto.idphotomaster.core.data.datasource.remote.UserRemoteDataSourceImpl
import com.idphoto.idphotomaster.core.data.repository.BasketRepository
import com.idphoto.idphotomaster.core.data.repository.BasketRepositoryImpl
import com.idphoto.idphotomaster.core.data.repository.OrdersRepository
import com.idphoto.idphotomaster.core.data.repository.OrdersRepositoryImpl
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

    @Binds
    fun bindBasketRemoteDataSource(sourceImpl: BasketRemoteDataSourceImpl): BasketRemoteDataSource

    @Binds
    fun bindBasketRepository(basketRepository: BasketRepositoryImpl): BasketRepository

    @Binds
    fun bindOrdersRemoteDataSource(sourceImpl: OrdersRemoteDataSourceImpl): OrdersRemoteDataSource

    @Binds
    fun bindOrdersRepository(ordersRepository: OrdersRepositoryImpl): OrdersRepository
}
