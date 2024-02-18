package com.idphoto.idphotomaster.app

import androidx.compose.ui.graphics.vector.ImageVector
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.domain.model.base.DialogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel<MainViewState>() {
    override fun createInitialState(): MainViewState {
        return MainViewState()
    }

    fun showCustomDialog(
        title: String,
        message: String?,
        confirmText: String,
        dismissText: String? = null,
        icon: ImageVector? = null,
        confirmCallback: (() -> Unit)? = null,
        onDismissCallback: (() -> Unit)? = null
    ) {
        updateState {
            copy(
                showDialogEvent = triggered(
                    DialogModel(
                        title,
                        message.orEmpty(),
                        confirmText,
                        dismissText,
                        icon,
                        confirmCallback,
                        onDismissCallback
                    )
                )
            )
        }

    }

    fun onShowCustomDialogConsumed() {
        updateState { copy(showDialogEvent = consumed()) }
    }
}

data class MainViewState(
    val loading: Boolean = false,
    val showDialogEvent: StateEventWithContent<DialogModel> = consumed(),
) : IViewState