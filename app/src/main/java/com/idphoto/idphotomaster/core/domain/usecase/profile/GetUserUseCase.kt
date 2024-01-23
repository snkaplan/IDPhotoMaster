package com.idphoto.idphotomaster.core.domain.usecase.profile

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(uid: String): Flow<User> {
        return flow {
            val result = userRepository.getUser(uid)
            (result.getOrNull() ?: throw IllegalArgumentException("error message")).also {
                emit(User.fromFirebaseMap(it))
            }
        }
    }
}