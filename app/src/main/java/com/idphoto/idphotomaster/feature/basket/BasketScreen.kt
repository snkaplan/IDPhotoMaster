package com.idphoto.idphotomaster.feature.basket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.AppTopBar
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.White

@Composable
fun BasketScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: BasketViewModel = hiltViewModel()
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                else -> {}
            }
        }
    }
    viewState.photo?.let {
        ScreenContent(
            viewState = viewState,
            modifier = modifier.fillMaxSize(),
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun ScreenContent(
    viewState: BasketViewState,
    modifier: Modifier,
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

            }
        }
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