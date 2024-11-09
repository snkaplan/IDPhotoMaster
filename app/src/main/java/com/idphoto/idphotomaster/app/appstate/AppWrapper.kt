package com.idphoto.idphotomaster.app.appstate

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.app.MainViewModel
import com.idphoto.idphotomaster.app.navigation.AppNavHost
import com.idphoto.idphotomaster.core.data.util.NetworkMonitor
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppWrapper(
    networkMonitor: NetworkMonitor,
    modifier: Modifier = Modifier,
    appState: MainAppState = rememberAppState(
        networkMonitor = networkMonitor,
    ),
    mainViewModel: MainViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val notConnectedMessage = stringResource(R.string.no_connection)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = SnackbarDuration.Indefinite,
            )
        }
    }

    AppScaffold(
        modifier = modifier.semantics {
            testTagsAsResourceId = true
        },
        bottomBar = {
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        AppNavHost(
            modifier = Modifier.padding(it),
            navController = appState.navController,
            mainViewModel = mainViewModel
        )
    }
}
