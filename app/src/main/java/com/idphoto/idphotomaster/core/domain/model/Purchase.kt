package com.idphoto.idphotomaster.core.domain.model

data class Purchase(val userId: String, val purchaseId: String, val cdnUrl: String) {
    fun toFirebaseMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "user_id" to userId,
            "purchase_id" to purchaseId,
            "cdn_url" to cdnUrl
        )
    }

    companion object {
        fun fromFirebaseMap(map: Map<String, Any?>): Purchase {
            return Purchase(
                userId = map["user_id"].toString(),
                purchaseId = map["purchase_id"].toString(),
                cdnUrl = map["cdn_url"].toString(),
            )
        }
    }
}