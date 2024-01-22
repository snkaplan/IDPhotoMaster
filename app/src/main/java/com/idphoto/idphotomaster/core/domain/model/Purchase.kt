package com.idphoto.idphotomaster.core.domain.model

data class Purchase(val userId: String, val purchaseId: String, val cdnUrl: String) {
    fun toFirebaseMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "user_id" to userId,
            "purchase_id" to purchaseId,
            "cdn_url" to cdnUrl
        )
    }
}