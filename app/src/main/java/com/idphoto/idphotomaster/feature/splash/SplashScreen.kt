package com.idphoto.idphotomaster.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.palm.composestateevents.NavigationEventEffect

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToTutorial: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashUiState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationEventEffect(
        event = splashUiState.navigateToHome,
        onConsumed = viewModel::onNavigateToHomeConsumed,
        action = navigateToHome
    )
    NavigationEventEffect(
        event = splashUiState.navigateToTutorial,
        onConsumed = viewModel::onNavigateToTutorialConsumed,
        action = navigateToTutorial
    )
    ScreenContent()
}

@Composable
private fun ScreenContent() {
}