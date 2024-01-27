package com.idphoto.idphotomaster.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.LoadingView
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Pink
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.SectionTextColor

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToSavedPhotos: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val splashUiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                else -> {}
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.init()
    }
    ScreenContent(viewState = splashUiState, modifier = modifier.fillMaxSize(), navigateToLogin, navigateToSavedPhotos)
}

@Composable
private fun ScreenContent(
    viewState: ProfileViewState,
    modifier: Modifier,
    navigateToLogin: () -> Unit,
    navigateToSavedPhotos: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier, verticalArrangement = Arrangement.Top) {
        if (viewState.loading) {
            LoadingView(modifier = Modifier.fillMaxHeight(), backgroundColor = BackgroundColor)
        } else {
            Spacer(modifier = Modifier.height(20.dp))
            Icon(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .verticalScroll(scrollState),
                imageVector = Icons.Filled.Camera,
                contentDescription = "",
                tint = Pink
            )
            Spacer(modifier = Modifier.height(10.dp))
            UserInfo(
                viewState.loggedIn,
                viewState.user?.name,
                viewState.user?.lastName,
                viewState.user?.mail,
                navigateToLogin
            )
            Spacer(modifier = Modifier.height(20.dp))
            ProfileGeneralSettings(navigateToSavedPhotos)
            Spacer(modifier = Modifier.height(20.dp))
            ProfileGeneralInfo()
        }
    }
}

@Composable
fun UserInfo(isLoggedIn: Boolean, name: String?, lastName: String?, email: String?, navigateToLogin: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoggedIn) {
            if (name != null) {
                Text(text = "$name $lastName", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
            }
            if (email != null) {
                Text(text = email, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp))
            }
        } else {
            Button(onClick = navigateToLogin) {
                Text(text = stringResource(id = R.string.login))
            }
        }
    }
}

@Composable
fun ProfileGeneralSettings(navigateToSavedPhotos: () -> Unit) {
    Column {
        ProfileSectionHeader(stringResource(id = R.string.general_settings))
        ProfileSectionItem(Icons.Default.Language, stringResource(id = R.string.language)) {}
        ProfileSectionItem(
            Icons.Default.StarOutline,
            stringResource(id = R.string.saved_photos),
            onClick = navigateToSavedPhotos
        )
    }
}

@Composable
fun ProfileGeneralInfo() {
    Column {
        ProfileSectionHeader(stringResource(id = R.string.information))
        ProfileSectionItem(Icons.Default.PhoneAndroid, stringResource(id = R.string.about)) {}
        ProfileSectionItem(Icons.Default.FilePresent, stringResource(id = R.string.terms_and_conditions)) {}
        ProfileSectionItem(Icons.Default.Security, stringResource(id = R.string.privacy_polciy)) {}
    }
}

@Composable
fun ProfileSectionHeader(sectionTitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
    ) {
        Text(
            text = sectionTitle,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 30.dp),
            style = TextStyle(
                fontSize = 18.sp,
                color = SectionTextColor,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun ProfileSectionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 30.dp),
            imageVector = icon,
            contentDescription = ""
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier.padding(horizontal = 30.dp),
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "",
            tint = SectionTextColor
        )
    }
}