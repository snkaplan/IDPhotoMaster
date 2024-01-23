package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignupUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(
        name: String,
        lastName: String,
        mail: String,
        password: String
    ): Flow<Unit> {
        return flow {
            val result = userRepository.createUserWithEmailPassword(mail, password)
            if (result.isSuccess) {
                val create = userRepository.createUser(
                    User(
                        userId = userRepository.currentUser?.uid,
                        name = name,
                        lastName = lastName,
                        mail = mail
                    )
                )
                (create.getOrNull() ?: throw IllegalArgumentException("error message")).also {
                    emit(it)
                }
            } else {
                throw IllegalArgumentException("error message")
            }
        }
    }
}