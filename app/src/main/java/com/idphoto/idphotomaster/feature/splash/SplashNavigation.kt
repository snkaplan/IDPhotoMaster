package com.idphoto.idphotomaster.feature.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SplashNavigationRoute = "splash_route"

fun NavGraphBuilder.splashScreen(
    navigateToHome: () -> Unit,
    navigateToTutorial: () -> Unit
) {
    composable(SplashNavigationRoute) {
        SplashScreen(
            navigateToHome = navigateToHome,
            navigateToTutorial = navigateToTutorial
        )
    }
}