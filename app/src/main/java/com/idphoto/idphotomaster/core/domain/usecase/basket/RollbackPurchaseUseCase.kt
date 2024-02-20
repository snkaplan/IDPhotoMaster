package com.idphoto.idphotomaster.core.domain.usecase.basket

import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.data.repository.BasketRepository
import getExceptionOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RollbackPurchaseUseCase @Inject constructor(private val basketRepository: BasketRepository) {
    operator fun invoke(userId: String, docId: String): Flow<Unit> {
        return flow {
            val result = basketRepository.deletePurchase(userId, "$userId-$docId${Constants.TempFileExtension}", docId)
            (result.getOrNull() ?: throw result.getExceptionOrDefault()).also {
                emit(it)
            }
        }
    }
}