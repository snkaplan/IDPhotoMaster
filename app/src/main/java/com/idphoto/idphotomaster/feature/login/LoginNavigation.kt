package com.idphoto.idphotomaster.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.idphoto.idphotomaster.app.MainViewModel

const val LoginNavigationRoute = "login_route"

fun NavController.navigateLogin(navOptions: NavOptions? = null) {
    this.navigate(LoginNavigationRoute, navOptions)
}

fun NavGraphBuilder.loginScreen(onBackClick: () -> Unit, mainViewModel: MainViewModel) {
    composable(LoginNavigationRoute) {
        LoginScreen(onBackClick = onBackClick, mainViewModel = mainViewModel)
    }
}