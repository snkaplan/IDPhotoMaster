package com.idphoto.idphotomaster.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val HomeNavigationRoute = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(HomeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(navigateToEditPhoto: (String) -> Unit) {
    composable(route = HomeNavigationRoute) {
        HomeScreen(navigateToEditPhoto)
    }
}
