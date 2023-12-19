package com.idphoto.idphotomaster.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is HomeViewEvents.NavigateCategory -> {

                }
            }
        }
    }
    ScreenContent(
        viewState = viewState,
        modifier = modifier
    )
}

@Composable
private fun ScreenContent(
    viewState: HomeViewState,
    modifier: Modifier
) {

}