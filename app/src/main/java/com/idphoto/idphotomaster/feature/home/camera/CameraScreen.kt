package com.idphoto.idphotomaster.feature.home.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import de.palm.composestateevents.NavigationEventEffect
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    navigateToEditPhoto: (String) -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val cameraState: CameraViewState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationEventEffect(
        event = cameraState.navigateToEditPhoto,
        onConsumed = viewModel::onNavigateToEditPhotoConsumed,
        action = navigateToEditPhoto
    )
    ErrorDialog(
        exception = cameraState.exception,
        onDismissRequest = {
            viewModel.onErrorDialogDismiss()
        },
        onPrimaryButtonClick = {
            viewModel.onErrorDialogDismiss()
        },
    )
    CameraContent(
        onPhotoCaptured = viewModel::saveTempImage,
        lastCapturedPhoto = cameraState.capturedImage,
        isLoading = cameraState.loading,
        showTutorialDialog = cameraState.showTutorialDialog,
        onViewEvent = viewModel::onTriggerViewEvent
    )
}

@Composable
private fun CameraContent(
    onPhotoCaptured: (Bitmap) -> Unit,
    lastCapturedPhoto: Bitmap? = null,
    isLoading: Boolean = false,
    showTutorialDialog: Boolean = false,
    onViewEvent: (CameraViewEvent) -> Unit
) {
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }

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
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        setBackgroundColor(android.graphics.Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
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
                        capturePhoto(
                            context,
                            cameraController,
                            onPhotoCaptured,
                        )
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
                    onPhotoCaptured
                )
            }

            if (isLoading) {
                LoadingView()
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            image.setCropRect(Rect())
            val correctedBitmap: Bitmap = image
                .toBitmap()
                .rotateBitmap(image.imageInfo.rotationDegrees)
            cameraController.unbind()
            onPhotoCaptured(correctedBitmap)
            image.close()
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraContent", "Error capturing image", exception)
        }
    })
}

@Composable
private fun LastPhotoPreview(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val capturedPhoto: ImageBitmap =
        remember(lastCapturedPhoto.hashCode()) { lastCapturedPhoto.asImageBitmap() }
    Card(
        modifier = modifier
            .size(128.dp)
            .padding(16.dp)
            .clickable {
                onPhotoCaptured.invoke(lastCapturedPhoto)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialDialog(onViewEvent: (CameraViewEvent) -> Unit) {
    BasicAlertDialog(onDismissRequest = {
        onViewEvent.invoke(CameraViewEvent.OnTutorialClosed)
    }) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable {
                        onViewEvent.invoke(CameraViewEvent.OnTutorialClosed)
                    }
                    .align(Alignment.End),
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color.White
            )
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Red)
                    .size(400.dp),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(id = R.drawable.ic_camera_tutorial),
                contentDescription = "Tutorial Image"
            )
        }
    }
}