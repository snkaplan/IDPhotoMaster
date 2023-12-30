package com.idphoto.idphotomaster.feature.editphoto

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.AppTopBar
import com.idphoto.idphotomaster.core.systemdesign.components.DrawLineWithDot
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.White

@Composable
fun EditPhotoScreen(
    modifier: Modifier = Modifier,
    viewModel: EditPhotoViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val splashUiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
        }
    }
    ScreenContent(
        viewState = splashUiState,
        modifier = modifier.fillMaxSize(),
        onBackClick = onBackClick
    )
}

@Composable
private fun ScreenContent(
    viewState: EditPhotoViewState,
    modifier: Modifier,
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .imePadding()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewState.lastCapturedPhoto?.let { safePhoto ->
                PhotoView(lastCapturedPhoto = safePhoto)
                Spacer(modifier = Modifier.height(15.dp))
                DrawLineWithDot(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.height(15.dp))
                EditField()
                Spacer(modifier = Modifier.height(15.dp))
                ScreenButton(text = stringResource(id = R.string.continue_text)) {

                }
                Spacer(modifier = Modifier.height(15.dp))
                ScreenButton(text = stringResource(id = R.string.save_changes)) {

                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun PhotoView(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap
) {
    val capturedPhoto: ImageBitmap =
        remember(lastCapturedPhoto.hashCode()) { lastCapturedPhoto.asImageBitmap() }
    Column(
        modifier = modifier
            .fillMaxWidth(0.75f)
            .height(350.dp)
    ) {
        Image(
            bitmap = capturedPhoto,
            contentDescription = "Last captured photo",
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
fun EditField() {
    var checked by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = BackgroundColor),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            EditItem(stringResource(id = R.string.sharpness))
            EditItem(stringResource(id = R.string.brightness))
            EditItem(stringResource(id = R.string.heat))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.remove_background))
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = Color.Green)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItem(itemTitle: String) {
    var sliderPosition by remember { mutableFloatStateOf(0.1f) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(text = itemTitle)
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.Black,
                activeTickColor = Color.Black,
                inactiveTickColor = Color.Black,
                inactiveTrackColor = Color.Black
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .border(4.dp, Color.Black, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        )
    }
}

@Composable
fun ScreenButton(text: String, onAction: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = Blue),
        onClick = { onAction.invoke() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = White)
        )
    }
}