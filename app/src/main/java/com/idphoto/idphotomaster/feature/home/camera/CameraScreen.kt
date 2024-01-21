package com.idphoto.idphotomaster.feature.home.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.core.common.extension.rotateBitmap
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
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
                        setBackgroundColor(Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
            Surface(
                shape = CircleShape, modifier = Modifier
                    .padding(20.dp)
                    .size(60.dp)
                    .align(Alignment.BottomCenter)
                    .clickable {
                        capturePhoto(context, cameraController, onPhotoCaptured)
                    }, color = androidx.compose.ui.graphics.Color.White
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
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(BackgroundColor.copy(alpha = 0.5f))
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
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
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
    }
}

@Preview
@Composable
private fun Preview_CameraContent() {
    CameraContent(
        onPhotoCaptured = {}
    )
}