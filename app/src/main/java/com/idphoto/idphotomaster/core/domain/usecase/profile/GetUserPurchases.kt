package com.idphoto.idphotomaster.core.domain.usecase.profile

import com.idphoto.idphotomaster.core.data.repository.OrdersRepository
import com.idphoto.idphotomaster.core.domain.model.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserPurchases @Inject constructor(private val ordersRepository: OrdersRepository) {
    operator fun invoke(uid: String): Flow<List<Purchase>> {
        return flow {
            val result = ordersRepository.getUserPurchases(uid)
            (result.getOrNull() ?: throw IllegalArgumentException("error message")).also {
                val list = mutableListOf<Purchase>()
                it.forEach { item ->
                    item.data?.let { safeData ->
                        list.add(Purchase.fromFirebaseMap(safeData))
                    }
                }
                emit(list)
            }
        }
    }
}