package com.idphoto.idphotomaster.feature.home

import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeViewState>() {
    override fun createInitialState(): HomeViewState = HomeViewState()
}

data class HomeViewState(
    val loading: Boolean = false,
) : IViewState