package com.idphoto.idphotomaster.feature.editphoto

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

const val EditPhotoNavigationRoute = "edit_photo_route"
internal const val photoPathArg = "photoPathArg"

internal class EditPhotoArgs(val photoPath: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                URLDecoder.decode(
                    checkNotNull(savedStateHandle[photoPathArg]),
                    URL_CHARACTER_ENCODING
                )
            )
}

fun NavGraphBuilder.editPhotoScreen(onBackClick: () -> Unit) {
    composable(
        route = "$EditPhotoNavigationRoute/{$photoPathArg}",
        arguments = listOf(
            navArgument(photoPathArg) { type = NavType.StringType },
        ),
    ) {
        EditPhotoScreen(onBackClick = onBackClick)
    }
}

fun NavController.navigateToEditPhoto(navOptions: NavOptions? = null, capturedImagePath: String) {
    val encodedPath = URLEncoder.encode(capturedImagePath, URL_CHARACTER_ENCODING)
    this.navigate("$EditPhotoNavigationRoute/$encodedPath", navOptions)
}
