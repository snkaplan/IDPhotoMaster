package com.idphoto.idphotomaster.app

import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
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
        dismissText: String? = null
    ) {
        fireEvent(MainViewEvents.ShowCustomDialog(title, message, confirmText, dismissText))
    }
}

data class MainViewState(val loading: Boolean = false) : IViewState

sealed class MainViewEvents : IViewEvents {
    data class ShowCustomDialog(
        val title: String,
        val message: String?,
        val confirmText: String?,
        val dismissText: String?
    ) : MainViewEvents()
}