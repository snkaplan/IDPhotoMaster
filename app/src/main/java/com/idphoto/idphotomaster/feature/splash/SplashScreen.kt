package com.idphoto.idphotomaster.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
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
        onConsumed = {
            viewModel.onTriggerViewEvent(SplashViewEvent.OnNavigateToHomeConsumed)
        },
        action = navigateToHome
    )
    NavigationEventEffect(
        event = splashUiState.navigateToTutorial,
        onConsumed = {
            viewModel.onTriggerViewEvent(SplashViewEvent.OnNavigateToTutorialConsumed)
        },
        action = navigateToTutorial
    )
    ScreenContent()
}

@Composable
private fun ScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = "Splash Icon"
        )
    }
}