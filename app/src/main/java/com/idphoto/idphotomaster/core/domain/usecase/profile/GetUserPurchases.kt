package com.idphoto.idphotomaster.core.domain.usecase.profile

import android.content.Context
import com.idphoto.idphotomaster.core.common.Constants.CachedFileName
import com.idphoto.idphotomaster.core.data.repository.OrdersRepository
import com.idphoto.idphotomaster.core.domain.model.Purchase
import com.idphoto.idphotomaster.core.domain.model.UserSavedPhoto
import dagger.hilt.android.qualifiers.ApplicationContext
import getExceptionOrDefault
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class GetUserPurchases @Inject constructor(
    private val ordersRepository: OrdersRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(uid: String): Flow<List<UserSavedPhoto>> {
        return flow {
            coroutineScope {
                val purchases = async { ordersRepository.getUserPurchases(uid) }
                val files = getCachedPhotos()
                val result = purchases.await()
                val filesResponse = files.await()
                (result.getOrNull() ?: throw result.getExceptionOrDefault()).also {
                    val list = mutableListOf<UserSavedPhoto>()
                    it.forEach { item ->
                        item.data?.let { safeData ->
                            list.add(UserSavedPhoto(Purchase.fromFirebaseMap(safeData).cdnUrl, true))
                        }
                    }
                    filesResponse.forEach { fileItem ->
                        list.add(UserSavedPhoto(fileItem.path, false))
                    }
                    emit(list)
                }
            }
        }
    }

    private suspend fun getCachedPhotos(): Deferred<List<File>> {
        val list = mutableListOf<File>()
        val cacheDir = context.cacheDir
        if (cacheDir.isDirectory) {
            val files: Array<out File>? = cacheDir.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.name.startsWith(CachedFileName)) {
                        list.add(f)
                    }
                }
            }
        }
        return CompletableDeferred(list)
    }
}