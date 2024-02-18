package com.idphoto.idphotomaster.core.domain.usecase.basket

import android.graphics.Bitmap
import com.google.firebase.firestore.FirebaseFirestore
import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.data.repository.BasketRepository
import com.idphoto.idphotomaster.core.domain.model.Purchase
import getExceptionOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class StartPurchaseUseCase @Inject constructor(
    private val basketRepository: BasketRepository,
    private val firebaseFirestore: FirebaseFirestore
) {
    operator fun invoke(userId: String, image: Bitmap): Flow<String> {
        return flow {
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val data = stream.toByteArray()
            val ref = firebaseFirestore.collection(Constants.USERS_TABLE_NAME).document(userId)
                .collection(Constants.PURCHASE_TABLE_NAME).document()
            val filename = "$userId-${ref.id}${Constants.TempFileExtension}"
            val upload = basketRepository.uploadPhoto(filename, data)
            if (upload.isSuccess) {
                val result =
                    basketRepository.purchase(userId, Purchase(userId, ref.id, upload.getOrNull().toString()), ref)
                (result.getOrNull() ?: throw result.getExceptionOrDefault()).also {
                    emit(ref.id)
                }
            } else {
                throw upload.getExceptionOrDefault()
            }
        }
    }
}