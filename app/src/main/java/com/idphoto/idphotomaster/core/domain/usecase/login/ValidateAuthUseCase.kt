package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.common.Constants.MinPasswordLength
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordLengthException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.MailRequiredException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateAuthUseCase @Inject constructor() {
    operator fun invoke(username: String, password: String): Flow<Resource<Unit>> {
        return flow {
            if (username.isEmpty()) {
                throw MailRequiredException()
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