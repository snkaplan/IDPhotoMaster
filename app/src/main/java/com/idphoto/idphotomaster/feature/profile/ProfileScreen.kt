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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.BuildConfig
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.domain.model.AppLanguageItem
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionType
import com.idphoto.idphotomaster.core.systemdesign.components.Dialog
import com.idphoto.idphotomaster.core.systemdesign.components.ErrorDialog
import com.idphoto.idphotomaster.core.systemdesign.components.InformationBottomSheet
import com.idphoto.idphotomaster.core.systemdesign.components.LoadingView
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Pink
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.SectionTextColor
import de.palm.composestateevents.NavigationEventEffect

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToSavedPhotos: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val locale = getLocale()
    LaunchedEffect(key1 = true) {
        viewModel.init(context, locale)
    }
    NavigationEventEffect(
        event = profileState.navigateToSavedPhotos,
        onConsumed = {
            viewModel.onTriggerEvent(ProfileViewTriggeredEvent.NavigateToSavedPhotosConsumed)
        },
        action = navigateToSavedPhotos
    )
    NavigationEventEffect(
        event = profileState.navigateToLogin,
        onConsumed = {
            viewModel.onTriggerEvent(ProfileViewTriggeredEvent.NavigateToLoginConsumed)
        },
        action = navigateToLogin
    )
    ErrorDialog(
        exception = profileState.exception,
        onDismissRequest = {
            viewModel.onTriggerEvent(ProfileViewTriggeredEvent.DismissErrorDialog)
        },
        onPrimaryButtonClick = {
            viewModel.onTriggerEvent(ProfileViewTriggeredEvent.DismissErrorDialog)
            if (profileState.exception?.exceptionType == ExceptionType.REQUIRES_AUTHORIZATION) {
                navigateToLogin.invoke()
            }
        },
    )
    ScreenContent(
        viewState = profileState,
        modifier = modifier.fillMaxSize(),
        onViewEvent = viewModel::onTriggerEvent
    )
}

@Composable
private fun ScreenContent(
    viewState: ProfileViewState,
    modifier: Modifier,
    onViewEvent: (ProfileViewTriggeredEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState), verticalArrangement = Arrangement.Top) {
        if (viewState.loading) {
            LoadingView(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterHorizontally),
                backgroundColor = BackgroundColor
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))
            Icon(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                imageVector = Icons.Filled.Camera,
                contentDescription = "Camera",
                tint = Pink
            )
            Spacer(modifier = Modifier.height(10.dp))
            UserInfo(
                viewState.loggedIn,
                viewState.user?.name,
                viewState.user?.lastName,
                viewState.user?.mail,
                onViewEvent
            )
            Spacer(modifier = Modifier.height(20.dp))
            ProfileGeneralSettings(
                viewState.showLanguageBottomSheet,
                viewState.languageList,
                onViewEvent
            )
            Spacer(modifier = Modifier.height(20.dp))
            ProfileGeneralInfo(onViewEvent = onViewEvent)
            if (viewState.infoBottomSheet != null) {
                InformationBottomSheet(
                    onDismissRequest = { onViewEvent.invoke(ProfileViewTriggeredEvent.InfoBottomSheetDismissed) },
                    infoBottomSheetItem = viewState.infoBottomSheet
                )
            }
            if (viewState.loggedIn) {
                Spacer(modifier = Modifier.height(20.dp))
                ProfileOtherSection(onViewEvent = onViewEvent)
            }
            Spacer(modifier = Modifier.height(20.dp))
            VersionContent()
        }
    }
}

@Composable
fun UserInfo(
    isLoggedIn: Boolean,
    name: String?,
    lastName: String?,
    email: String?,
    onViewEvent: (ProfileViewTriggeredEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoggedIn) {
            if (name != null) {
                Text(text = "$name $lastName", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
            }
            if (email != null) {
                Text(text = email, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp))
            }
        } else {
            Button(onClick = {
                onViewEvent.invoke(ProfileViewTriggeredEvent.NavigateToLogin)
            }) {
                Text(text = stringResource(id = R.string.login))
            }
        }
    }
}

@Composable
fun ProfileGeneralSettings(
    showLanguageBottomSheet: Boolean?,
    languageList: List<AppLanguageItem>?,
    onViewEvent: (ProfileViewTriggeredEvent) -> Unit
) {
    Column {
        ProfileSectionHeader(stringResource(id = R.string.general_settings))
        ProfileSectionItem(Icons.Default.Language, stringResource(id = R.string.language)) {
            onViewEvent.invoke(ProfileViewTriggeredEvent.ShowLanguageBottomSheet)
        }
        ProfileSectionItem(
            Icons.Default.StarOutline,
            stringResource(id = R.string.saved_photos),
            onClick = {
                onViewEvent.invoke(ProfileViewTriggeredEvent.NavigateToSavedPhotos)
            }
        )
    }
    if (showLanguageBottomSheet == true) {
        val context = LocalContext.current
        ChangeLanguageBottomSheet(
            modifier = Modifier.heightIn(max = 300.dp),
            onDismissRequest = { onViewEvent.invoke(ProfileViewTriggeredEvent.LanguageBottomSheetDismissed) },
            options = languageList,
            onConfirm = { selectedCode ->
                selectedCode?.let { safeCode ->
                    onViewEvent.invoke(ProfileViewTriggeredEvent.ChangeLanguage(context, safeCode))
                }
            }
        )
    }
}

@Composable
@ReadOnlyComposable
fun getLocale(): java.util.Locale {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0) ?: LocaleListCompat.getDefault()[0]!!
}

@Composable
fun ProfileGeneralInfo(onViewEvent: (ProfileViewTriggeredEvent) -> Unit) {
    val context = LocalContext.current
    val currentLanguage = getLocale().language
    Column {
        ProfileSectionHeader(stringResource(id = R.string.information))
        ProfileSectionItem(Icons.Default.PhoneAndroid, stringResource(id = R.string.about)) {
            onViewEvent(
                ProfileViewTriggeredEvent.ShowInfoBottomSheet(
                    context.getString(R.string.about),
                    GeneralInfoType.About,
                    currentLanguage
                )
            )
        }
        ProfileSectionItem(Icons.Default.FilePresent, stringResource(id = R.string.terms_and_conditions)) {
            onViewEvent(
                ProfileViewTriggeredEvent.ShowInfoBottomSheet(
                    context.getString(R.string.terms_and_conditions),
                    GeneralInfoType.TermsAndConditions,
                    currentLanguage
                )
            )
        }
        ProfileSectionItem(Icons.Default.Security, stringResource(id = R.string.privacy_policy)) {
            onViewEvent(
                ProfileViewTriggeredEvent.ShowInfoBottomSheet(
                    context.getString(R.string.privacy_policy),
                    GeneralInfoType.PrivacyPolicy,
                    currentLanguage
                )
            )
        }
    }
}

@Composable
fun ProfileOtherSection(onViewEvent: (ProfileViewTriggeredEvent) -> Unit) {
    var showDeleteAccountPopUp by remember { mutableStateOf(false) }
    ProfileSectionHeader(stringResource(id = R.string.other))
    ProfileSectionItem(Icons.AutoMirrored.Default.Logout, stringResource(id = R.string.logout)) {
        onViewEvent(ProfileViewTriggeredEvent.Logout)
    }
    ProfileSectionItem(Icons.Default.NoAccounts, stringResource(id = R.string.delete_account)) {
        showDeleteAccountPopUp = true
    }
    if (showDeleteAccountPopUp) {
        Dialog(
            title = stringResource(id = R.string.delete_account_title),
            description = stringResource(id = R.string.delete_account_description),
            primaryButtonText = stringResource(id = R.string.delete_account),
            secondaryButtonText = stringResource(id = R.string.cancel),
            primaryButtonClick = {
                onViewEvent.invoke(ProfileViewTriggeredEvent.DeleteAccountConfirmed)
                showDeleteAccountPopUp = false
            },
            primaryButtonColor = Color.Red,
            secondaryButtonClick = { showDeleteAccountPopUp = false }) {
            showDeleteAccountPopUp = false
        }
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
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 30.dp),
            imageVector = icon,
            contentDescription = "Profile Item Icon"
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
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = "Go",
            tint = SectionTextColor
        )
    }
}

@Composable
fun VersionContent() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            textAlign = TextAlign.End,
            text = stringResource(id = R.string.version, BuildConfig.VERSION_NAME)
        )
    }
}