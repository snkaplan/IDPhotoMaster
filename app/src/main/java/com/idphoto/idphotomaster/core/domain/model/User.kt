package com.idphoto.idphotomaster.core.domain.model

data class User(
    val userId: String?,
    val name: String,
    val lastName: String,
    val mail: String
) {
    fun toFirebaseMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "user_id" to userId,
            "name" to name,
            "last_name" to lastName,
            "mail" to mail
        )
    }

    companion object {
        fun fromFirebaseMap(map: Map<String, Any?>): User {
            return User(
                userId = map["user_id"].toString(),
                name = map["name"].toString(),
                lastName = map["last_name"].toString(),
                mail = map["mail"].toString()
            )
        }
    }
}