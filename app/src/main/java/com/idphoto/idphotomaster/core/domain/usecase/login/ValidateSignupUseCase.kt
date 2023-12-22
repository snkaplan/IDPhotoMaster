package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.common.Constants.MinPasswordLength
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.domain.exceptions.LastNameRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.MailRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.NameRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordLengthException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordsNotMatchingException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateSignupUseCase @Inject constructor() {
    operator fun invoke(
        name: String,
        lastName: String,
        mail: String,
        password: String,
        passwordAgain: String
    ): Flow<Resource<Unit>> {
        return flow {
            if (mail.isEmpty()) {
                throw MailRequiredException()
            }
            if (name.isEmpty()) {
                throw NameRequiredException()
            }
            if (lastName.isEmpty()) {
                throw LastNameRequiredException()
            }
            if (password.isEmpty() || passwordAgain.isEmpty()) {
                throw PasswordRequiredException()
            }
            if (password.length < MinPasswordLength) {
                throw PasswordLengthException()
            }
            if (password != passwordAgain) {
                throw PasswordsNotMatchingException()
            }
            emit(Unit)
        }.asResource()
    }
}