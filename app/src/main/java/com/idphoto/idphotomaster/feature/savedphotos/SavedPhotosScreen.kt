package com.idphoto.idphotomaster.feature.savedphotos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.domain.model.UserSavedPhoto
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.AppTopBar
import com.idphoto.idphotomaster.core.systemdesign.components.CoilImageComponent
import com.idphoto.idphotomaster.core.systemdesign.components.Dialog
import com.idphoto.idphotomaster.core.systemdesign.components.ErrorDialog
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.utils.DisableScreenshot
import com.idphoto.idphotomaster.core.systemdesign.utils.findActivity

@Composable
fun SavedPhotosScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: SavedPhotosViewModel = hiltViewModel()
) {
    val activity = LocalContext.current.findActivity()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.init()
    }

    DisableScreenshot(activity)
    ErrorDialog(
        exception = uiState.exception,
        onDismissRequest = {
            viewModel.onErrorDialogDismiss()
        },
        onPrimaryButtonClick = {
            viewModel.onErrorDialogDismiss()
        },
    )
    ScreenContent(
        viewState = uiState,
        modifier = modifier.fillMaxSize(),
        onViewEvent = viewModel::onTriggerViewEvent,
        onBackClick = onBackClick
    )
}

@Composable
private fun ScreenContent(
    viewState: SavedPhotosViewState,
    modifier: Modifier,
    onViewEvent: (SavedPhotosViewEvent) -> Unit,
    onBackClick: () -> Unit
) {
    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                leftIcon = AppIcons.BackIcon,
                onLeftIconClicked = onBackClick
            )
        },
    ) { padding ->
        if (viewState.showPhotoSavedDialog) {
            Dialog(
                title = stringResource(id = R.string.photo_saved_to_gallery),
                description = "",
                primaryButtonText = stringResource(id = R.string.ok),
                primaryButtonClick = { onViewEvent.invoke(SavedPhotosViewEvent.PhotoSavedDialogDismissed) }
            ) {
                onViewEvent.invoke(SavedPhotosViewEvent.PhotoSavedDialogDismissed)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .imePadding(),
            verticalArrangement = Arrangement.Top,
        ) {
            if (viewState.loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    color = Blue
                )
            }
            viewState.savedPhotos?.let {
                if (it.isEmpty() && viewState.loading.not()) {
                    EmptySavedPhotosScreen()
                } else {
                    PhotoList(
                        it,
                        onViewEvent = onViewEvent
                    )
                }
            } ?: runCatching {
                if (viewState.loading.not()) {
                    EmptySavedPhotosScreen()
                }
            }
        }
    }
}

@Composable
fun EmptySavedPhotosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp),
            imageVector = Icons.Filled.PhotoLibrary,
            contentDescription = "Photos",
            tint = Blue
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.saved_photos),
            style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.saved_photos_desc),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun PhotoList(
    photos: List<UserSavedPhoto>,
    onViewEvent: (SavedPhotosViewEvent) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        columns = GridCells.Fixed(count = 2)
    ) {
        itemsIndexed(photos) { _, item ->
            PhotoItem(
                savedPhoto = item,
                onViewEvent = onViewEvent
            )
        }
    }
}

@Composable
fun PhotoItem(
    savedPhoto: UserSavedPhoto,
    onViewEvent: (SavedPhotosViewEvent) -> Unit
) {
    val context = LocalContext.current
    var showSaveDialog by remember { mutableStateOf(false) }
    if (showSaveDialog) {
        Dialog(
            title = stringResource(id = R.string.save_to_gallery),
            description = stringResource(id = R.string.save_to_gallery_desc),
            primaryButtonText = stringResource(id = R.string.ok),
            secondaryButtonText = stringResource(id = R.string.cancel),
            primaryButtonClick = {
                showSaveDialog = false
                onViewEvent.invoke(SavedPhotosViewEvent.OnBoughtPhotoClicked(savedPhoto.cdnUrl, context))
            },
            secondaryButtonClick = {
                showSaveDialog = false
            }
        ) {
            showSaveDialog = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 250.dp)
            .background(color = Color.White)
            .border(4.dp, BackgroundColor, RoundedCornerShape(10.dp))
            .padding(4.dp)
            .clickable {
                if (savedPhoto.bought) {
                    showSaveDialog = true
                }
            }
    ) {
        CoilImageComponent(imageUrl = savedPhoto.cdnUrl)
        if (savedPhoto.bought) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "Bought",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                tint = Color.Unspecified
            )
        }
    }
}