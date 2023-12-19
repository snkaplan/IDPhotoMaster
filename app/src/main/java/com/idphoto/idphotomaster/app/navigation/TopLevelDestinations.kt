package com.idphoto.idphotomaster.app.navigation

import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.icon.Icon
import com.idphoto.idphotomaster.feature.home.HomeNavigationRoute

enum class TopLevelDestinations(
    val route: String,
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val titleTextId: Int,
) {
    HOME(
        route = HomeNavigationRoute,
        selectedIcon = Icon.ImageVectorIcon(AppIcons.Home),
        unselectedIcon = Icon.ImageVectorIcon(AppIcons.HomeOutlined),
        titleTextId = R.string.empty,
    )
}