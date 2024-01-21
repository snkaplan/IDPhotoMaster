package com.idphoto.idphotomaster.feature.basket

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.app.MainViewModel
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.AppTopBar
import com.idphoto.idphotomaster.core.systemdesign.components.DrawLineWithDot
import com.idphoto.idphotomaster.core.systemdesign.components.PhotoView
import com.idphoto.idphotomaster.core.systemdesign.components.ScreenButton
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue

@Composable
fun BasketScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    navigateToLogin: () -> Unit,
    onCompletePurchase: () -> Unit,
    viewModel: BasketViewModel = hiltViewModel(),
    googlePurchaseViewModel: GooglePurchaseViewModel = hiltViewModel(),
    mainViewModel: MainViewModel
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                BasketViewEvent.NavigateToLogin -> navigateToLogin()
                BasketViewEvent.StartGooglePurchase -> googlePurchaseViewModel.purchase("id_photo", context as Activity)
            }
        }
    }
    LaunchedEffect(key1 = googlePurchaseViewModel.uiEvents) {
        googlePurchaseViewModel.uiEvents.collect { event ->
            when (event) {
                GooglePurchaseViewEvent.PurchaseSuccess -> {
                    viewModel.purchaseSuccess()
                    mainViewModel.showCustomDialog(
                        title = context.getString(R.string.purchase_success_title),
                        message = context.getString(R.string.purchase_success_description),
                        confirmText = context.getString(R.string.ok),
                        confirmCallback = { onCompletePurchase.invoke() },
                        onDismissCallback = { onCompletePurchase.invoke() }
                    )
                }

                GooglePurchaseViewEvent.PurchaseFailed -> {
                    mainViewModel.showCustomDialog(
                        title = context.getString(R.string.purchase_failed_title),
                        message = context.getString(R.string.purchase_failed_description),
                        confirmText = context.getString(R.string.ok)
                    )
                }

                GooglePurchaseViewEvent.UserCancelledPurchase -> {
                    mainViewModel.showCustomDialog(
                        title = context.getString(R.string.purchase_failed_title),
                        message = context.getString(R.string.purchase_user_cancelled_description),
                        confirmText = context.getString(R.string.ok)
                    )
                }
            }
        }
    }
    LaunchedEffect(key1 = true) {
        googlePurchaseViewModel.billingSetup(context)
        googlePurchaseViewModel.checkProducts()
    }
    viewState.photo?.let {
        ScreenContent(
            viewState = viewState,
            modifier = modifier.fillMaxSize(),
            onBackClick = onBackClick,
            onCompletePurchase = viewModel::onCompletePurchase
        )
    }
}

@Composable
private fun ScreenContent(
    viewState: BasketViewState,
    modifier: Modifier,
    onBackClick: () -> Unit,
    onCompletePurchase: () -> Unit
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
        ) {
            if (viewState.loading) {
                LinearProgressIndicator(color = Blue)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viewState.photo?.let { safePhoto ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundColor)
                    ) {
                        PhotoView(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White),
                            bitmap = safePhoto.asImageBitmap()
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    DropDown(modifier = Modifier.fillMaxWidth(0.75f))
                    Spacer(modifier = Modifier.height(15.dp))
                    DrawLineWithDot(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ScreenButton(
                        text = stringResource(id = R.string.complete_purchase),
                        onAction = onCompletePurchase
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun DropDown(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("")
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(16.dp)
            .height(IntrinsicSize.Min)
            .clickable(onClick = { expanded = expanded.not() })
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(id = R.string.select),
                color = Color.Black,
                fontSize = 16.sp
            )
            Icon(
                if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { label ->
                DropdownMenuItem(text = { Text(text = label) }, onClick = { })
            }
        }
    }
}