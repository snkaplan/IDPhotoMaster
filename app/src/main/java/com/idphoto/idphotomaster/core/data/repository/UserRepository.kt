package com.idphoto.idphotomaster.core.data.repository

import com.google.firebase.auth.FirebaseUser
import com.idphoto.idphotomaster.core.domain.model.User

interface UserRepository {
    val currentUser: FirebaseUser?
    suspend fun createUserWithEmailPassword(email: String, password: String): Result<FirebaseUser>
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun googleSignIn(token: String): Result<FirebaseUser>
    suspend fun createUser(user: User): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun getUser(uid: String): Result<Map<String, Any?>>
    suspend fun deleteUser(): Result<Unit>
}