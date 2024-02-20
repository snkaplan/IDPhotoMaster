package com.idphoto.idphotomaster.feature.savedphotos

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SavedPhotosNavigationRoute = "saved_photos_route"

fun NavGraphBuilder.savedPhotosScreen(
    onBackClick: () -> Unit
) {
    composable(SavedPhotosNavigationRoute) {
        SavedPhotosScreen(onBackClick = onBackClick)
    }
}

fun NavController.navigateToSavedPhotos(navOptions: NavOptions? = null) {
    this.navigate(SavedPhotosNavigationRoute, navOptions)
}