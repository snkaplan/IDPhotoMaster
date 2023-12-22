package com.idphoto.idphotomaster.feature.tutorial

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val TutorialNavigationRoute = "tutorial_route"

fun NavGraphBuilder.tutorialScreen(navigateToLogin: () -> Unit, navigateToHome: () -> Unit) {
    composable(TutorialNavigationRoute) {
        TutorialScreen(navigateToLogin = navigateToLogin, navigateToHome = navigateToHome)
    }
}

fun NavController.navigateToTutorial(navOptions: NavOptions? = null) {
    this.navigate(TutorialNavigationRoute, navOptions)
}
