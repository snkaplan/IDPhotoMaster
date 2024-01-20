package com.idphoto.idphotomaster.feature.basket

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

const val BasketNavigationRoute = "basket_route"
internal const val basketPhotoPathArg = "basketPhotoPath"

internal class BasketArgs(val selectedPhotoPath: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                URLDecoder.decode(
                    checkNotNull(savedStateHandle[basketPhotoPathArg]),
                    URL_CHARACTER_ENCODING
                )
            )
}

fun NavGraphBuilder.basketScreen(onBackClick: () -> Unit, navigateToLogin: () -> Unit) {
    composable(
        route = "$BasketNavigationRoute/{$basketPhotoPathArg}",
        arguments = listOf(
            navArgument(basketPhotoPathArg) { type = NavType.StringType },
        ),
    ) {
        BasketScreen(onBackClick = onBackClick, navigateToLogin = navigateToLogin)
    }
}

fun NavController.navigateToBasket(navOptions: NavOptions? = null, capturedImagePath: String) {
    val encodedPath = URLEncoder.encode(capturedImagePath, URL_CHARACTER_ENCODING)
    this.navigate("$BasketNavigationRoute/$encodedPath", navOptions)
}
