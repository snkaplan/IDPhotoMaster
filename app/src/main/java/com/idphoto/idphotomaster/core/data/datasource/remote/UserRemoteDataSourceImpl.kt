package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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

    override suspend fun googleSignIn(token: String): Result<FirebaseUser> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(token, null)
            val result = auth.signInWithCredential(credential).await()
            result.user!!
        }
    }

    override suspend fun createUser(user: MutableMap<String, Any?>, docId: String?): Result<Unit> {
        return runCatching {
            if (docId != null) {
                firebaseFirestore.collection(USERS_TABLE_NAME).document(docId).set(user).await()
            }
        }
    }

    override suspend fun getUser(uid: String): Result<Map<String, Any?>> {
        return runCatching {
            val result = firebaseFirestore.collection(USERS_TABLE_NAME).document(uid).get().await()
            result.data ?: throw Exception()
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            auth.signOut()
        }
    }
}