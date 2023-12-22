package com.idphoto.idphotomaster.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.idphoto.idphotomaster.app.MainViewModel
import com.idphoto.idphotomaster.feature.home.homeScreen
import com.idphoto.idphotomaster.feature.home.navigateToHome
import com.idphoto.idphotomaster.feature.login.LoginNavigationRoute
import com.idphoto.idphotomaster.feature.login.loginScreen
import com.idphoto.idphotomaster.feature.login.navigateLogin
import com.idphoto.idphotomaster.feature.splash.SplashNavigationRoute
import com.idphoto.idphotomaster.feature.splash.splashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = SplashNavigationRoute,
    mainViewModel: MainViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        splashScreen(
            navigateToLogin = {
                navController.navigateLogin(
                    navOptions = navOptions {
                        popUpTo(SplashNavigationRoute) {
                            inclusive = true
                        }
                    },
                )
            },
            navigateToHome = {
                navController.navigateToHome(
                    navOptions = navOptions {
                        popUpTo(SplashNavigationRoute) {
                            inclusive = true
                        }
                    },
                )
            }
        )
        homeScreen()
        loginScreen(navigateToHome = {
            navController.navigateToHome(
                navOptions = navOptions {
                    popUpTo(LoginNavigationRoute) {
                        inclusive = true
                    }
                }
            )
        }, mainViewModel = mainViewModel)
    }
}