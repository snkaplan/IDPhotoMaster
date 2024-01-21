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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.core.common.extension.rotateBitmap
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.LoadingView
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    navigateToEditPhoto: (String) -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val cameraState: CameraViewState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is CameraViewEvents.NavigateToEditPhoto -> navigateToEditPhoto.invoke(event.capturedImageUri.toString())
            }
        }
    }
    CameraContent(
        onPhotoCaptured = viewModel::saveTempImage,
        lastCapturedPhoto = cameraState.capturedImage,
        isLoading = cameraState.loading
    )
}

@Composable
private fun CameraContent(
    onPhotoCaptured: (Bitmap) -> Unit,
    lastCapturedPhoto: Bitmap? = null,
    isLoading: Boolean = false
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
            val currentDensity = LocalDensity.current
            val configuration = LocalConfiguration.current
            Surface(
                shape = CircleShape, modifier = Modifier
                    .padding(20.dp)
                    .size(60.dp)
                    .align(Alignment.BottomCenter)
                    .clickable {
                        val offsetInPx: Float
                        val widthInPx: Float
                        val heightInPx: Float
                        val width: Float
                        with(currentDensity) {
                            offsetInPx = 150.dp.toPx()
                            widthInPx = 354.dp.toPx()
                            heightInPx = 472.dp.toPx()
                            width = configuration.screenWidthDp.dp.toPx()
                        }
                        capturePhoto(
                            context,
                            cameraController,
                            onPhotoCaptured,
                            cropX = (width - widthInPx).toInt() / 2,
                            cropY = offsetInPx.toInt(),
                            cropWidth = widthInPx.toInt(),
                            cropHeight = heightInPx.toInt()
                        )
                    }, color = Color.White
            ) {
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
    onPhotoCaptured: (Bitmap) -> Unit,
    cropX: Int, cropY: Int, cropWidth: Int, cropHeight: Int
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            image.setCropRect(Rect())
            val correctedBitmap: Bitmap = image
                .toBitmap()
                .rotateBitmap(image.imageInfo.rotationDegrees)
            val croppedBitmap = Bitmap.createBitmap(
                correctedBitmap,
                cropX,
                cropY,
                cropWidth,
                cropHeight
            )
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
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
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

@Preview
@Composable
private fun Preview_CameraContent() {
    CameraContent(
        onPhotoCaptured = {}
    )
}