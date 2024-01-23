package com.idphoto.idphotomaster.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.idphoto.idphotomaster.core.data.datasource.remote.UserRemoteDataSource
import com.idphoto.idphotomaster.core.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun createUserWithEmailPassword(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return userRemoteDataSource.createUserWithEmailPassword(email, password)
    }

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return userRemoteDataSource.login(email, password)
    }

    override suspend fun createUser(user: User): Result<Unit> {
        return userRemoteDataSource.createUser(user.toFirebaseMap(), user.userId)
    }

    override suspend fun signOut(): Result<Unit> {
        return userRemoteDataSource.signOut()
    }

    override suspend fun getUser(uid: String): Result<Map<String, Any?>> {
        return userRemoteDataSource.getUser(uid)
    }
}