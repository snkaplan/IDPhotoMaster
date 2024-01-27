package com.idphoto.idphotomaster.feature.savedphotos

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.domain.model.UserSavedPhoto
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.AppTopBar
import com.idphoto.idphotomaster.core.systemdesign.components.CoilImageComponent
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue

@Composable
fun SavedPhotosScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    navigateToEditScreen: (String) -> Unit,
    viewModel: SavedPhotosViewModel = hiltViewModel()
) {
    val splashUiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is SavedPhotoViewEvents.NavigateToEditPhotoWithPath -> navigateToEditScreen(event.path)
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.init()
    }
    ScreenContent(
        viewState = splashUiState,
        modifier = modifier.fillMaxSize(),
        onBackClick = onBackClick,
        onSavedPhotoClicked = viewModel::onSavedPhotoClicked,
        onBoughtPhotoClicked = viewModel::onBoughtPhotoClicked
    )
}

@Composable
private fun ScreenContent(
    viewState: SavedPhotosViewState,
    modifier: Modifier,
    onSavedPhotoClicked: (String) -> Unit,
    onBoughtPhotoClicked: (Context, String) -> Unit,
    onBackClick: () -> Unit,
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
                PhotoList(
                    it,
                    onSavedPhotoClicked = { safePath ->
                        if (viewState.loading.not()) {
                            onSavedPhotoClicked.invoke(safePath)
                        }
                    },
                    onBoughtPhotoClicked = { ctx, url ->
                        if (viewState.loading.not()) {
                            onBoughtPhotoClicked.invoke(ctx, url)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PhotoList(
    photos: List<UserSavedPhoto>,
    onSavedPhotoClicked: (String) -> Unit,
    onBoughtPhotoClicked: (Context, String) -> Unit
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
                onSavedPhotoClicked = onSavedPhotoClicked,
                onBoughtPhotoClicked = onBoughtPhotoClicked
            )
        }
    }
}

@Composable
fun PhotoItem(
    savedPhoto: UserSavedPhoto,
    onSavedPhotoClicked: (String) -> Unit,
    onBoughtPhotoClicked: (Context, String) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 250.dp)
            .background(color = Color.White)
            .border(4.dp, BackgroundColor, RoundedCornerShape(10.dp))
            .padding(4.dp)
            .clickable {
                if (savedPhoto.bought.not()) {
                    onSavedPhotoClicked.invoke(savedPhoto.cdnUrl)
                } else {
                    onBoughtPhotoClicked(context, savedPhoto.cdnUrl)
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