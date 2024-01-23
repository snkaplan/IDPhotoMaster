package com.idphoto.idphotomaster.app.navigation

import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.icon.Icon
import com.idphoto.idphotomaster.feature.home.HomeNavigationRoute
import com.idphoto.idphotomaster.feature.profile.ProfileNavigationRoute

enum class TopLevelDestinations(
    val route: String,
    val selectedIcon: Icon,
    val unselectedIcon: Icon
) {
    HOME(
        route = HomeNavigationRoute,
        selectedIcon = Icon.ImageVectorIcon(AppIcons.Camera),
        unselectedIcon = Icon.ImageVectorIcon(AppIcons.CameraOutlined)
    ),
    PROFILE(
        route = ProfileNavigationRoute,
        selectedIcon = Icon.DrawableResourceIcon(AppIcons.ProfileSelected),
        unselectedIcon = Icon.DrawableResourceIcon(AppIcons.ProfileUnselected)
    )
}