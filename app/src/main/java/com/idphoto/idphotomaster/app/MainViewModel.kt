package com.idphoto.idphotomaster.app

import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.systemdesign.components.DialogItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel<MainViewState, MainViewEvents>() {
    override fun createInitialState(): MainViewState {
        return MainViewState()
    }

    fun showCustomDialog(
        title: String,
        message: String?,
        confirmText: String?,
        dismissText: String? = null,
        confirmCallback: (() -> Unit)? = null,
        onDismissCallback: (() -> Unit)? = null
    ) {
        fireEvent(
            MainViewEvents.ShowCustomDialog(
                DialogItem(
                    title,
                    message,
                    confirmText,
                    dismissText,
                    confirmCallback,
                    onDismissCallback
                )
            )
        )
    }
}

data class MainViewState(val loading: Boolean = false) : IViewState

sealed class MainViewEvents : IViewEvents {
    data class ShowCustomDialog(
        val dialogItem: DialogItem
    ) : MainViewEvents()
}