package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.exceptions.GeneralException
import com.idphoto.idphotomaster.core.domain.model.LoginResult
import com.idphoto.idphotomaster.core.domain.model.User
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
                userRepository.createUser(
                    User(
                        userId = user?.uid,
                        name = nameList?.first().orEmpty(),
                        lastName = nameList?.last().orEmpty(),
                        mail = user?.email.orEmpty()
                    )
                )
                (user ?: throw GeneralException()).also {
                    emit(LoginResult(user.uid, user.uid))
                }
            } else {
                throw GeneralException()
            }
        }
    }
}