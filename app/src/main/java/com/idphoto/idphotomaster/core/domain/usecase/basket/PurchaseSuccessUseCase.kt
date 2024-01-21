package com.idphoto.idphotomaster.core.domain.usecase.basket

import com.idphoto.idphotomaster.core.data.repository.BasketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PurchaseSuccessUseCase @Inject constructor(private val basketRepository: BasketRepository) {
    operator fun invoke(): Flow<Unit> {
        return flow {
            val result = basketRepository.purchase()
            if (result.isSuccess) {
                val upload = basketRepository.uploadPhoto()
                (upload.getOrNull() ?: throw IllegalArgumentException("error message")).also {
                    emit(it)
                }
            } else {
                throw IllegalArgumentException("error message")
            }
        }
    }
}