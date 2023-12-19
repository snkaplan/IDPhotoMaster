package com.idphoto.idphotomaster.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface IViewState
interface IViewEvents

abstract class BaseViewModel<State : IViewState, Events : IViewEvents> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }

    abstract fun createInitialState(): State

    protected val currentState: State get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState

    private val _uiEvents: MutableSharedFlow<Events> = MutableSharedFlow()
    val uiEvents: SharedFlow<Events> = _uiEvents

    protected fun updateState(reduce: State.() -> State) {
        _uiState.update { currentState.reduce() }
    }

    protected fun fireEvent(event: Events) {
        viewModelScope.launch {
            _uiEvents.emit(event)
        }
    }
}