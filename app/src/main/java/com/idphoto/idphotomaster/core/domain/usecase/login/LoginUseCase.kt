package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.exceptions.GeneralException
import com.idphoto.idphotomaster.core.domain.model.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(mail: String, password: String): Flow<LoginResult> {
        return flow {
            val result = userRepository.login(mail, password)
            (result.getOrNull() ?: throw GeneralException()).also {
                emit(LoginResult(it.uid, it.uid))
            }
        }
    }
}