package com.idphoto.idphotomaster.feature.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SplashNavigationRoute = "splash_route"

fun NavGraphBuilder.splashScreen(navigateToLogin: () -> Unit, navigateToHome: () -> Unit) {
    composable(SplashNavigationRoute) {
        SplashScreen(navigateToLogin = navigateToLogin, navigateToHome = navigateToHome)
    }
}