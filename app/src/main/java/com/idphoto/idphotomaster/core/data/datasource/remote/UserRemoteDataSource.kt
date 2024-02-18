package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.auth.FirebaseUser

interface UserRemoteDataSource {
    suspend fun createUserWithEmailPassword(email: String, password: String): Result<FirebaseUser>
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun googleSignIn(token: String): Result<FirebaseUser>
    suspend fun createUser(user: MutableMap<String, Any?>, docId: String?): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun getUser(uid: String): Result<Map<String, Any?>>
    suspend fun deleteUser(): Result<Unit>
}