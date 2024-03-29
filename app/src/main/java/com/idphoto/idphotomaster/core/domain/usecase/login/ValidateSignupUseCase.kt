package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.common.Constants.MinPasswordLength
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.domain.exceptions.LastNameRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.MailRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.NameRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordLengthException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.TermsAndConditionsNotAcceptedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateSignupUseCase @Inject constructor() {
    operator fun invoke(
        name: String,
        lastName: String,
        mail: String,
        password: String,
        isTermsAndConditionsChecked: Boolean
    ): Flow<Resource<Unit>> {
        return flow {
            if (name.isEmpty()) {
                throw NameRequiredException()
            }
            if (lastName.isEmpty()) {
                throw LastNameRequiredException()
            }
            if (mail.isEmpty()) {
                throw MailRequiredException()
            }
            if (password.isEmpty()) {
                throw PasswordRequiredException()
            }
            if (password.length < MinPasswordLength) {
                throw PasswordLengthException()
            }
            if (isTermsAndConditionsChecked.not()) {
                throw TermsAndConditionsNotAcceptedException()
            }
            emit(Unit)
        }.asResource()
    }
}