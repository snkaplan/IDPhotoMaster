package com.idphoto.idphotomaster.feature.home.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.common.extension.rotateBitmap
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.ErrorDialog
import com.idphoto.idphotomaster.core.systemdesign.components.LoadingView
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.utils.takePicture
import de.palm.composestateevents.NavigationEventEffect

@Composable
fun CameraScreen(
    navigateToEditPhoto: (String) -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val cameraState: CameraViewState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationEventEffect(
        event = cameraState.navigateToEditPhoto,
        onConsumed = {
            viewModel.onTriggerViewEvent(CameraViewEvent.OnNavigateToEditPhotoConsumed)
        },
        action = navigateToEditPhoto
    )
    ErrorDialog(
        exception = cameraState.exception,
        onDismissRequest = {
            viewModel.onTriggerViewEvent(CameraViewEvent.DismissErrorDialog)
        },
        onPrimaryButtonClick = {
            viewModel.onTriggerViewEvent(CameraViewEvent.DismissErrorDialog)
        },
    )
    CameraContent(
        lastCapturedPhoto = cameraState.capturedImage,
        isLoading = cameraState.loading,
        showTutorialDialog = cameraState.showTutorialDialog,
        onViewEvent = viewModel::onTriggerViewEvent
    )
}

@Composable
private fun CameraContent(
    lastCapturedPhoto: Bitmap? = null,
    isLoading: Boolean = false,
    showTutorialDialog: Boolean = false,
    onViewEvent: (CameraViewEvent) -> Unit
) {
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {},
    ) { padding ->
        Box(
            modifier = Modifier
                .background(BackgroundColor)
                .padding(padding)
                .imePadding()
        ) {
            CameraPreview()
            TransparentClipLayout(
                modifier = Modifier.fillMaxSize(),
                width = 354.dp,
                height = 472.dp,
                offsetY = 150.dp
            )
            Image(
                painter = painterResource(id = R.drawable.ic_camera_overlay),
                contentDescription = "",
                modifier = Modifier
                    .align(Center)
                    .fillMaxWidth()
                    .height(472.dp)
            )
            Surface(
                shape = CircleShape, modifier = Modifier
                    .padding(20.dp)
                    .size(60.dp)
                    .align(Alignment.BottomCenter)
                    .clickable {
                        takePicture(context, onImageCaptured = { image ->
                            image.setCropRect(Rect())
                            val correctedBitmap: Bitmap = image
                                .toBitmap()
                                .rotateBitmap(image.imageInfo.rotationDegrees)
                            onViewEvent.invoke(CameraViewEvent.SaveImageAndNavigate(correctedBitmap))
                            image.close()
                        },
                            lifecycleOwner = lifecycleOwner, onError = {

                            })
                    }, color = Color.White
            ) {
            }
            Surface(
                shape = CircleShape, modifier = Modifier
                    .padding(20.dp)
                    .size(60.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        onViewEvent.invoke(CameraViewEvent.OnClickTutorial)
                    }, color = Color.Transparent
            ) {
                Image(painterResource(id = R.drawable.ic_tutorial_icon), contentDescription = "Tutorial")
            }

            if (showTutorialDialog) {
                TutorialDialog(onViewEvent)
            }

            if (lastCapturedPhoto != null) {
                LastPhotoPreview(
                    modifier = Modifier.align(alignment = BottomStart),
                    lastCapturedPhoto = lastCapturedPhoto,
                    onViewEvent = onViewEvent
                )
            }

            if (isLoading) {
                LoadingView()
            }
        }
    }
}


@Composable
private fun LastPhotoPreview(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap,
    onViewEvent: (CameraViewEvent) -> Unit
) {
    val capturedPhoto: ImageBitmap =
        remember(lastCapturedPhoto.hashCode()) { lastCapturedPhoto.asImageBitmap() }
    Card(
        modifier = modifier
            .size(128.dp)
            .padding(16.dp)
            .clickable {
                onViewEvent.invoke(CameraViewEvent.SaveImageAndNavigate(lastCapturedPhoto))
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Image(
            bitmap = capturedPhoto,
            contentDescription = "Last captured photo",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun TransparentClipLayout(
    modifier: Modifier,
    width: Dp,
    height: Dp,
    offsetY: Dp
) {
    val offsetInPx: Float
    val widthInPx: Float
    val heightInPx: Float
    with(LocalDensity.current) {
        offsetInPx = offsetY.toPx()
        widthInPx = width.toPx()
        heightInPx = height.toPx()
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)
            drawRect(Color(0x77000000))
            drawRoundRect(
                topLeft = Offset(
                    x = (canvasWidth - widthInPx) / 2,
                    y = offsetInPx
                ),
                size = Size(widthInPx, heightInPx),
                cornerRadius = CornerRadius(30f, 30f),
                color = Color.Transparent,
                blendMode = BlendMode.Clear
            )
            restoreToCount(checkPoint)
        }
    }
}

@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val preview = Preview.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                    .build()
            )
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(ctx))
        previewView
    })
}