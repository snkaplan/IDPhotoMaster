package com.idphoto.idphotomaster.core.data.datasource.remote

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.idphoto.idphotomaster.core.common.Constants.PURCHASE_TABLE_NAME
import com.idphoto.idphotomaster.core.common.Constants.USERS_TABLE_NAME
import com.idphoto.idphotomaster.core.common.await
import com.idphoto.idphotomaster.core.data.util.NetworkMonitor
import javax.inject.Inject

private const val IMAGES_FOLDER = "images"

class BasketRemoteDataSourceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val networkMonitor: NetworkMonitor,
) : BasketRemoteDataSource {
    override suspend fun purchase(
        uid: String, purchase: MutableMap<String, Any?>, documentReference: DocumentReference
    ): Result<Unit> {
        return runCatching {
            documentReference.set(purchase).await(networkMonitor)
        }
    }

    override suspend fun uploadPhoto(fileName: String, image: ByteArray): Result<Uri> {
        return runCatching {
            val storageRef = firebaseStorage.reference.child(IMAGES_FOLDER).child(fileName)
            val upload = storageRef.putBytes(image).await(networkMonitor)
            val uri = upload.storage.downloadUrl.await(networkMonitor)
            uri
        }
    }

    override suspend fun deletePurchase(userId: String, id: String): Result<Unit> {
        return runCatching {
            val storageRef = firebaseStorage.reference.child(IMAGES_FOLDER).child(id)
            firebaseFirestore.collection(USERS_TABLE_NAME).document(userId).collection(PURCHASE_TABLE_NAME).document(id)
                .delete().await(networkMonitor)
            storageRef.delete().await(networkMonitor)
        }
    }
}