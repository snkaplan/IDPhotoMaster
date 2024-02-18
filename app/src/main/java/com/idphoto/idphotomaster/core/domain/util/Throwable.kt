import androidx.annotation.StringRes
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.domain.exceptions.NetworkException
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
    @StringRes primaryButtonTextResId: Int? = R.string.retry,
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