package com.idphoto.idphotomaster.core.data.datasource.remote

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.idphoto.idphotomaster.core.common.Constants.PURCHASE_TABLE_NAME
import com.idphoto.idphotomaster.core.common.await
import javax.inject.Inject

private const val IMAGES_FOLDER = "images"

class BasketRemoteDataSourceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : BasketRemoteDataSource {
    override suspend fun purchase(purchase: MutableMap<String, Any?>): Result<Unit> {
        return runCatching {
            firebaseFirestore.collection(PURCHASE_TABLE_NAME).add(purchase).await()
        }
    }

    override suspend fun uploadPhoto(fileName: String, image: ByteArray): Result<Uri> {
        return runCatching {
            val storageRef = firebaseStorage.reference.child(IMAGES_FOLDER).child(fileName)
            val upload = storageRef.putBytes(image).await()
            val uri = upload.storage.downloadUrl.await()
            uri
        }
    }
}