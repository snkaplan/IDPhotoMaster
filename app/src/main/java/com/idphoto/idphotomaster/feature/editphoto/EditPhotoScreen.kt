package com.idphoto.idphotomaster.feature.editphoto

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
    val context = LocalContext.current
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                EditPhotoViewEvent.PhotoReadCompleted -> viewModel.initImage(context)
            }
        }
    }
    viewState.updatedPhoto?.let {
        ScreenContent(
            viewState = viewState,
            modifier = modifier.fillMaxSize(),
            onBackClick = onBackClick,
            onBrightnessChanged = viewModel::onBrightnessChanged,
            onSharpnessChanged = viewModel::onSharpnessChanged,
            onHeatChanged = viewModel::onHeatChanged
        )
    }
}

@Composable
private fun ScreenContent(
    viewState: EditPhotoViewState,
    modifier: Modifier,
    onBackClick: () -> Unit,
    onBrightnessChanged: (brightness: Float) -> Unit,
    onSharpnessChanged: (brightness: Float) -> Unit,
    onHeatChanged: (brightness: Float) -> Unit
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
            viewState.updatedPhoto?.let { safePhoto ->
                PhotoView(bitmap = viewState.updatedPhoto.asImageBitmap())
                Spacer(modifier = Modifier.height(15.dp))
                DrawLineWithDot(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.height(15.dp))
                EditField(
                    viewState.brightness,
                    viewState.sharpness,
                    viewState.heat,
                    {
                        onBrightnessChanged.invoke(it)
                    },
                    {
                        onSharpnessChanged.invoke(it)
                    },
                    {
                        onHeatChanged(it)
                    }
                )
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
    bitmap: ImageBitmap
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.75f)
            .height(350.dp)
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = "Last captured photo",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun EditField(
    brightness: Float,
    contrast: Float,
    heat: Float,
    onBrightnessChanged: (brightness: Float) -> Unit,
    onSharpnessChanged: (contrast: Float) -> Unit,
    onHeatChanged: (heat: Float) -> Unit,
) {
    var checked by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = BackgroundColor),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            EditItem(stringResource(id = R.string.sharpness), contrast, 0f, 4f, onSharpnessChanged)
            EditItem(stringResource(id = R.string.brightness), brightness, -1f, 1f, onBrightnessChanged)
            EditItem(stringResource(id = R.string.heat), heat, 4000f, 7000f, onHeatChanged)
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
fun EditItem(
    itemTitle: String,
    initialValue: Float,
    minValue: Float,
    maxValue: Float,
    onChange: (newValue: Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(initialValue) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(text = itemTitle)
        Slider(
            value = sliderPosition,
            valueRange = minValue..maxValue,
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                onChange(sliderPosition)
            },
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