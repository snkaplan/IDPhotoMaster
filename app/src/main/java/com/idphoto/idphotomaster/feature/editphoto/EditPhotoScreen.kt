package com.idphoto.idphotomaster.feature.editphoto

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.AppTopBar
import com.idphoto.idphotomaster.core.systemdesign.components.DrawLineWithDot
import com.idphoto.idphotomaster.core.systemdesign.components.PhotoView
import com.idphoto.idphotomaster.core.systemdesign.components.ScreenButton
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.utils.DisableScreenshot
import com.idphoto.idphotomaster.core.systemdesign.utils.findActivity
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.NavigationEventEffect

@Composable
fun EditPhotoScreen(
    modifier: Modifier = Modifier,
    viewModel: EditPhotoViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    navigateToBasket: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    EventEffect(
        event = viewState.photoReadCompleted,
        onConsumed = viewModel::onPhotoReadCompletedConsumed,
        action = { viewModel.initImage(context) }
    )
    EventEffect(
        event = viewState.resetImage,
        onConsumed = viewModel::onResetImageConsumed,
        action = { viewModel.initImage(context) }
    )
    NavigationEventEffect(
        event = viewState.navigateToBasket,
        onConsumed = viewModel::onNavigateToBasketConsumed,
        action = navigateToBasket
    )

    DisableScreenshot(activity)
    viewState.updatedPhoto?.let {
        ScreenContent(
            viewState = viewState,
            modifier = modifier.fillMaxSize(),
            onBackClick = onBackClick,
            onBrightnessChanged = viewModel::onBrightnessChanged,
            onSharpnessChanged = viewModel::onSharpnessChanged,
            onHeatChanged = viewModel::onHeatChanged,
            onRemoveBackground = viewModel::onRemoveBackground,
            onSaveImage = viewModel::savePhoto,
            onContinue = viewModel::navigateToBasket
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
    onHeatChanged: (brightness: Float) -> Unit,
    onRemoveBackground: (remove: Boolean) -> Unit,
    onSaveImage: () -> Unit,
    onContinue: () -> Unit
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
                .imePadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewState.loading) {
                LinearProgressIndicator(color = Blue)
                Spacer(modifier = Modifier.height(10.dp))
            }
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viewState.updatedPhoto?.let { safePhoto ->
                    PhotoView(bitmap = safePhoto.asImageBitmap())
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
                        viewState.loading,
                        {
                            onBrightnessChanged.invoke(it)
                        },
                        {
                            onSharpnessChanged.invoke(it)
                        },
                        {
                            onHeatChanged(it)
                        },
                        onRemoveBackground
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    ScreenButton(
                        text = stringResource(id = R.string.continue_text),
                        onAction = onContinue,
                        enabled = viewState.loading.not()
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    ScreenButton(
                        text = stringResource(id = R.string.save_changes),
                        onAction = onSaveImage,
                        enabled = viewState.loading.not()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun EditField(
    brightness: Float,
    contrast: Float,
    heat: Float,
    loading: Boolean,
    onBrightnessChanged: (brightness: Float) -> Unit,
    onSharpnessChanged: (contrast: Float) -> Unit,
    onHeatChanged: (heat: Float) -> Unit,
    onRemoveBackground: (remove: Boolean) -> Unit
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
            EditItem(stringResource(id = R.string.sharpness), loading, contrast, 0f, 4f, onSharpnessChanged)
            EditItem(stringResource(id = R.string.brightness), loading, brightness, -1f, 1f, onBrightnessChanged)
            EditItem(stringResource(id = R.string.heat), loading, heat, 4000f, 7000f, onHeatChanged)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.remove_background))
                Switch(
                    enabled = loading.not(),
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        onRemoveBackground.invoke(checked)
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
    loading: Boolean,
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
            enabled = loading.not(),
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