package com.idphoto.idphotomaster.feature.home

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.idphoto.idphotomaster.feature.home.camera.CameraScreen
import com.idphoto.idphotomaster.feature.home.nopermission.NoCameraPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navigateToEditPhoto: (String) -> Unit) {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    ScreenContent(
        hasPermission = cameraPermissionState.status.isGranted,
        navigateToEditPhoto = navigateToEditPhoto,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
private fun ScreenContent(
    hasPermission: Boolean,
    navigateToEditPhoto: (String) -> Unit,
    onRequestPermission: () -> Unit
) {
    if (hasPermission) {
        CameraScreen(navigateToEditPhoto)
    } else {
        NoCameraPermissionScreen(onRequestPermission)
    }
}