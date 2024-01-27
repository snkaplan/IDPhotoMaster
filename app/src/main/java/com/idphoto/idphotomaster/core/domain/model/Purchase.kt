package com.idphoto.idphotomaster.core.domain.model

import com.google.firebase.firestore.FieldValue

data class Purchase(
    val userId: String,
    val purchaseId: String,
    val cdnUrl: String,
    val date: FieldValue? = FieldValue.serverTimestamp()
) {
    fun toFirebaseMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "user_id" to userId,
            "purchase_id" to purchaseId,
            "cdn_url" to cdnUrl,
            "date" to date
        )
    }

    companion object {
        fun fromFirebaseMap(map: Map<String, Any?>): Purchase {
            return Purchase(
                userId = map["user_id"].toString(),
                purchaseId = map["purchase_id"].toString(),
                cdnUrl = map["cdn_url"].toString(),
                date = map["date"] as? FieldValue
            )
        }
    }
}