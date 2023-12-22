package com.idphoto.idphotomaster.core.domain.model

data class User(
    val id: String? = null,
    val uid: String?,
    val name: String,
    val lastName: String,
    val mail: String
) {
    fun toFirebaseMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "user_id" to uid,
            "name" to name,
            "last_name" to lastName,
            "mail" to mail
        )
    }
}