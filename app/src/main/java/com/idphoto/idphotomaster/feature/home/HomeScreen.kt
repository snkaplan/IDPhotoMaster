package com.idphoto.idphotomaster.feature.home

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.idphoto.idphotomaster.feature.home.camera.CameraScreen
import com.idphoto.idphotomaster.feature.home.nopermission.NoCameraPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navigateToEditPhoto: (String) -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState: PermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is HomeViewEvents.NavigateCategory -> {

                }
            }
        }
    }
    ScreenContent(
        viewState = viewState,
        hasPermission = cameraPermissionState.status.isGranted,
        navigateToEditPhoto = navigateToEditPhoto,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
private fun ScreenContent(
    viewState: HomeViewState,
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