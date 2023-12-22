package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.idphoto.idphotomaster.core.common.await
import javax.inject.Inject

private const val USERS_TABLE_NAME = "users"

class UserRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : UserRemoteDataSource {
    override suspend fun createUserWithEmailPassword(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user!!
        }
    }

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return runCatching {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user!!
        }
    }

    override suspend fun createUser(user: MutableMap<String, Any?>): Result<Unit> {
        return runCatching {
            firebaseFirestore.collection(USERS_TABLE_NAME).add(user).await()
        }
    }
}