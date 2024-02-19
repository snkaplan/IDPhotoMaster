import androidx.annotation.StringRes
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.domain.exceptions.GeneralException
import com.idphoto.idphotomaster.core.domain.exceptions.NetworkException
import com.idphoto.idphotomaster.core.domain.exceptions.RequiresAuthorizationException
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionType
import com.idphoto.idphotomaster.core.domain.util.getIcon

fun Throwable?.getExceptionModel(
    title: String? = null,
    description: String? = null,
    primaryButtonText: String? = null,
    secondButtonText: String? = null,
    @StringRes titleResId: Int? = R.string.exception_title,
    @StringRes descriptionResId: Int? = null,
    @StringRes primaryButtonTextResId: Int? = null,
    @StringRes secondButtonTextResId: Int? = R.string.close,
): ExceptionModel {
    return when (this) {
        is NetworkException -> ExceptionModel(
            titleResId = R.string.network_exception_title,
            descriptionResId = R.string.network_exception_description,
            primaryButtonTextResId = R.string.retry,
            secondaryButtonTextResId = R.string.close,
            exceptionType = ExceptionType.NETWORK
        ).apply {
            icon = this.getIcon()
        }

        is RequiresAuthorizationException -> ExceptionModel(
            titleResId = R.string.exception_title,
            descriptionResId = R.string.exception_delete_user_re_authenticate,
            primaryButtonTextResId = R.string.login,
            secondaryButtonTextResId = R.string.close,
            exceptionType = ExceptionType.REQUIRES_AUTHORIZATION
        ).apply {
            icon = this.getIcon()
        }

        else -> ExceptionModel(
            title = title,
            titleResId = titleResId,
            description = description,
            descriptionResId = descriptionResId,
            primaryButtonText = primaryButtonText,
            primaryButtonTextResId = primaryButtonTextResId,
            secondaryButtonText = secondButtonText,
            secondaryButtonTextResId = secondButtonTextResId,
            exceptionType = ExceptionType.GENERAL
        ).apply { icon = this.getIcon() }
    }
}

fun Result<Any>.getExceptionOrDefault(): Throwable {
    val exception = this.exceptionOrNull()
    if (exception is FirebaseAuthRecentLoginRequiredException) {
        return RequiresAuthorizationException()
    }
    return exception ?: GeneralException()
}