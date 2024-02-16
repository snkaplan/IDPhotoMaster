import androidx.annotation.StringRes
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.domain.exceptions.NetworkException
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionType
import com.idphoto.idphotomaster.core.domain.util.getIcon

private fun Throwable?.getErrorMessage(
    title: String? = null,
    description: String? = null,
    primaryButtonText: String? = null,
    secondButtonText: String? = null,
    @StringRes titleResId: Int? = null,
    @StringRes descriptionResId: Int? = null,
    @StringRes primaryButtonTextResId: Int? = null,
    @StringRes secondButtonTextResId: Int? = null,
): ExceptionModel {
    return when (this) {
        is NetworkException -> ExceptionModel(
            titleResId = R.string.network_exception_title,
            descriptionResId = R.string.network_exception_description,
            primaryButtonTextResId = R.string.retry,
            secondButtonTextResId = R.string.close,
            exceptionType = ExceptionType.NETWORK
        )

        else -> ExceptionModel(
            title = title,
            titleResId = titleResId,
            description = description,
            descriptionResId = descriptionResId,
            primaryButtonText = primaryButtonText,
            primaryButtonTextResId = primaryButtonTextResId,
            secondButtonText = secondButtonText,
            secondButtonTextResId = secondButtonTextResId,
            exceptionType = ExceptionType.GENERAL
        )
    }
}

fun Throwable.getExceptionModel(): ExceptionModel {
    val model = this.getErrorMessage()
    return model.copy(
        icon = model.getIcon(),
    )
}