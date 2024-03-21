package com.idphoto.idphotomaster.feature.splash

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.Dialog
import de.palm.composestateevents.NavigationEventEffect

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToTutorial: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashUiState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationEventEffect(
        event = splashUiState.navigateToHome,
        onConsumed = {
            viewModel.onTriggerViewEvent(SplashViewEvent.OnNavigateToHomeConsumed)
        },
        action = navigateToHome
    )
    NavigationEventEffect(
        event = splashUiState.navigateToTutorial,
        onConsumed = {
            viewModel.onTriggerViewEvent(SplashViewEvent.OnNavigateToTutorialConsumed)
        },
        action = navigateToTutorial
    )
    ScreenContent()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermissionDialog(viewModel::onTriggerViewEvent)
    } else {
        viewModel.onTriggerViewEvent(SplashViewEvent.HandleNavigation)
    }
}

@Composable
private fun ScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = "Splash Icon"
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(
    onViewEvent: (SplashViewEvent) -> Unit
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    var showRationaleDialog by remember { mutableStateOf(true) }
    if (permissionState.status.isGranted) {
        onViewEvent.invoke(SplashViewEvent.HandleNavigation)
    } else {
        if (!permissionState.status.isGranted && permissionState.status.shouldShowRationale && showRationaleDialog) {
            Dialog(
                title = stringResource(id = R.string.notification_permission_title),
                description = stringResource(id = R.string.notification_permission_description),
                primaryButtonText = stringResource(id = R.string.allow),
                primaryButtonClick = {
                    permissionState.launchPermissionRequest()
                    showRationaleDialog = false
                },
                secondaryButtonText = stringResource(id = R.string.close),
                secondaryButtonClick = {
                    showRationaleDialog = false
                    onViewEvent.invoke(SplashViewEvent.HandleNavigation)
                },
                onDismissRequest = {
                    showRationaleDialog = false
                    onViewEvent.invoke(SplashViewEvent.HandleNavigation)
                }
            )
        } else {
            // permission granted or forever denied
            LaunchedEffect(key1 = Unit, block = { permissionState.launchPermissionRequest() })
            showRationaleDialog = false
            onViewEvent.invoke(SplashViewEvent.HandleNavigation)
        }
    }
}