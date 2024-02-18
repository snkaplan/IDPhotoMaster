package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.LoginResult
import com.idphoto.idphotomaster.core.domain.model.User
import getExceptionOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(token: String): Flow<LoginResult> {
        return flow {
            val result = userRepository.googleSignIn(token)
            if (result.isSuccess) {
                val user = result.getOrNull()
                val nameList = user?.displayName?.split(" ")
                val create = userRepository.createUser(
                    User(
                        userId = user?.uid,
                        name = nameList?.first().orEmpty(),
                        lastName = nameList?.last().orEmpty(),
                        mail = user?.email.orEmpty()
                    )
                )
                (user ?: throw create.getExceptionOrDefault()).also {
                    emit(LoginResult(user.uid, user.uid))
                }
            } else {
                throw result.getExceptionOrDefault()
            }
        }
    }
}