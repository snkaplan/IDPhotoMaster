package com.idphoto.idphotomaster.core.data.repository

import com.google.firebase.auth.FirebaseUser
import com.idphoto.idphotomaster.core.domain.model.User

interface UserRepository {
    val currentUser: FirebaseUser?
    suspend fun createUserWithEmailPassword(email: String, password: String): Result<FirebaseUser>
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun createUser(user: User): Result<Unit>
}