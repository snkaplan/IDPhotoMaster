package com.idphoto.idphotomaster.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.idphoto.idphotomaster.app.MainViewModel
import com.idphoto.idphotomaster.feature.basket.basketScreen
import com.idphoto.idphotomaster.feature.basket.navigateToBasket
import com.idphoto.idphotomaster.feature.editphoto.editPhotoScreen
import com.idphoto.idphotomaster.feature.editphoto.navigateToEditPhoto
import com.idphoto.idphotomaster.feature.home.HomeNavigationRoute
import com.idphoto.idphotomaster.feature.home.homeScreen
import com.idphoto.idphotomaster.feature.home.navigateToHome
import com.idphoto.idphotomaster.feature.splash.SplashNavigationRoute
import com.idphoto.idphotomaster.feature.splash.splashScreen
import com.idphoto.idphotomaster.feature.tutorial.TutorialNavigationRoute
import com.idphoto.idphotomaster.feature.tutorial.navigateToTutorial
import com.idphoto.idphotomaster.feature.tutorial.tutorialScreen

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
            navigateToHome = {
                navController.navigateToHome(
                    navOptions = navOptions {
                        popUpTo(SplashNavigationRoute) {
                            inclusive = true
                        }
                    },
                )
            },
            navigateToTutorial = {
                navController.navigateToTutorial(
                    navOptions = navOptions {
                        popUpTo(SplashNavigationRoute) {
                            inclusive = true
                        }
                    },
                )
            }
        )
        tutorialScreen(navigateToHome = {
            navController.navigateToHome(
                navOptions = navOptions {
                    popUpTo(TutorialNavigationRoute) {
                        inclusive = true
                    }
                }
            )
        })
        homeScreen(navigateToEditPhoto = {
            navController.navigateToEditPhoto(capturedImagePath = it)
        })
        editPhotoScreen(navController::popBackStack) {
            navController.navigateToBasket(capturedImagePath = it)
        }
        basketScreen(navController::popBackStack, onCompletePurchase = {
            navController.popBackStack(route = HomeNavigationRoute, inclusive = false)
        }, mainViewModel = mainViewModel)
    }
}