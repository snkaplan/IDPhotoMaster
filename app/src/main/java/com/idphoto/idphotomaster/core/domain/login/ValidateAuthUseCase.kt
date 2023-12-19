package com.idphoto.idphotomaster.core.domain.login

import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val MinUsernameLength = 6
private const val MinPasswordLength = 6

class ValidateAuthUseCase @Inject constructor() {
    operator fun invoke(username: String, password: String): Flow<Resource<Unit>> {
        return flow {
            if (username.isEmpty()) {
                throw UsernameRequiredException()
            }
            if (username.length < MinUsernameLength) {
                throw UsernameLengthException()
            }
            if (password.isEmpty()) {
                throw PasswordRequiredException()
            }
            if (password.length < MinPasswordLength) {
                throw PasswordLengthException()
            }
            emit(Unit)
        }.asResource()
    }
}

class UsernameRequiredException : Exception()
class PasswordRequiredException : Exception()
class UsernameLengthException : Exception()
class PasswordLengthException : Exception()
