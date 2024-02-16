package com.idphoto.idphotomaster.core.domain.usecase.basket

import android.graphics.Bitmap
import com.idphoto.idphotomaster.core.data.repository.BasketRepository
import com.idphoto.idphotomaster.core.domain.model.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class StartPurchaseUseCase @Inject constructor(private val basketRepository: BasketRepository) {
    operator fun invoke(userId: String, purchaseId: String, image: Bitmap): Flow<Unit> {
        return flow {
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val data = stream.toByteArray()
            val filename = "$userId-$purchaseId.png"
            val upload = basketRepository.uploadPhoto(filename, data)
            if (upload.isSuccess) {
                val result =
                    basketRepository.purchase(userId, Purchase(userId, purchaseId, upload.getOrNull().toString()))
                (result.getOrNull() ?: throw IllegalArgumentException("error message")).also {
                    emit(it)
                }
            } else {
                throw IllegalArgumentException("error message")
            }
        }
    }
}