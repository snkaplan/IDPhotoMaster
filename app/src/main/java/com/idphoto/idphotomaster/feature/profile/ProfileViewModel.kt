package com.idphoto.idphotomaster.feature.profile

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.AppLanguageItem
import com.idphoto.idphotomaster.core.domain.model.InfoBottomSheetItem
import com.idphoto.idphotomaster.core.domain.model.User
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.usecase.config.GetConfigUseCase
import com.idphoto.idphotomaster.core.domain.usecase.profile.DeleteUserUseCase
import com.idphoto.idphotomaster.core.domain.usecase.profile.GetUserUseCase
import com.idphoto.idphotomaster.core.domain.usecase.profile.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import getExceptionModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : BaseViewModel<ProfileViewState>() {
    override fun createInitialState(): ProfileViewState = ProfileViewState()

    fun init(context: Context, locale: Locale) {
        getConfig()
        val languageList = listOf(
            AppLanguageItem(context.getString(R.string.turkish), "tr", locale.language == "tr"),
            AppLanguageItem(context.getString(R.string.english), "en", locale.language == "en")
        )
        updateState { copy(languageList = languageList) }
        userRepository.currentUser?.let {
            getUser(it.uid)
        }
    }

    fun onTriggerEvent(event: ProfileViewTriggeredEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileViewTriggeredEvent.ShowInfoBottomSheet -> {
                    updateState {
                        copy(
                            infoBottomSheet = InfoBottomSheetItem(
                                event.title,
                                description = when (event.type) {
                                    GeneralInfoType.About -> config?.getString("about_app").orEmpty()
                                    GeneralInfoType.PrivacyPolicy -> config?.getString("privacy_policy").orEmpty()
                                    GeneralInfoType.TermsAndConditions -> config?.getString("terms_and_conditions")
                                        .orEmpty()
                                }
                            )
                        )
                    }
                }

                ProfileViewTriggeredEvent.InfoBottomSheetDismissed -> {
                    updateState { copy(infoBottomSheet = null) }
                }

                ProfileViewTriggeredEvent.LanguageBottomSheetDismissed -> {
                    updateState { copy(showLanguageBottomSheet = false) }
                }

                ProfileViewTriggeredEvent.ShowLanguageBottomSheet -> {
                    updateState { copy(showLanguageBottomSheet = true) }
                }

                is ProfileViewTriggeredEvent.ChangeLanguage -> {
                    changeLanguage(event.context, event.languageCode)
                    updateState { copy(showLanguageBottomSheet = false) }
                }

                ProfileViewTriggeredEvent.Logout -> {
                    logout()
                }

                ProfileViewTriggeredEvent.DeleteAccountConfirmed -> {
                    deleteAccount()
                }

                ProfileViewTriggeredEvent.NavigateToLogin -> updateState { copy(navigateToLogin = triggered) }
                ProfileViewTriggeredEvent.NavigateToLoginConsumed -> updateState { copy(navigateToLogin = consumed) }
                ProfileViewTriggeredEvent.NavigateToSavedPhotos -> updateState { copy(navigateToSavedPhotos = triggered) }
                ProfileViewTriggeredEvent.NavigateToSavedPhotosConsumed -> updateState { copy(navigateToSavedPhotos = consumed) }
                ProfileViewTriggeredEvent.DismissErrorDialog -> updateState { copy(exception = null) }
            }
        }
    }

    private fun getConfig() {
        viewModelScope.launch {
            getConfigUseCase.invoke().asResource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        updateState {
                            copy(
                                exception = result.exception?.getExceptionModel(
                                    descriptionResId = R.string.exception_fetch_config
                                )
                            )
                        }
                    }

                    Resource.Loading -> {}
                    is Resource.Success -> {
                        updateState { copy(config = result.data) }
                    }
                }
            }.launchIn(this)
        }
    }

    private fun getUser(uid: String) {
        viewModelScope.launch {
            getUserUseCase.invoke(uid).asResource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState {
                            copy(
                                loading = false, exception = result.exception?.getExceptionModel(
                                    descriptionResId = R.string.exception_fetch_user
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false, user = result.data, loggedIn = true) }
                    }
                }
            }.launchIn(this)
        }
    }

    private fun changeLanguage(context: Context, code: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags(code)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            deleteUserUseCase.invoke().asResource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState {
                            copy(
                                loading = false, exception = result.exception?.getExceptionModel(
                                    descriptionResId = R.string.exception_delete_user
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false, user = null, loggedIn = false) }
                    }
                }
            }.launchIn(this)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke().asResource().onEach {
                when (it) {
                    is Resource.Error -> {
                        updateState { copy(loading = false) }
                    }

                    Resource.Loading -> updateState { copy(loading = true) }
                    is Resource.Success -> {
                        updateState { copy(loading = false, loggedIn = false, user = null) }
                    }
                }
            }.launchIn(this)
        }
    }
}

data class ProfileViewState(
    val loading: Boolean = false,
    val loggedIn: Boolean = false,
    val user: User? = null,
    val infoBottomSheet: InfoBottomSheetItem? = null,
    val showLanguageBottomSheet: Boolean? = false,
    val languageList: List<AppLanguageItem>? = null,
    val config: FirebaseRemoteConfig? = null,
    val navigateToLogin: StateEvent = consumed,
    val navigateToSavedPhotos: StateEvent = consumed,
    val exception: ExceptionModel? = null
) : IViewState

sealed class ProfileViewTriggeredEvent {
    data class ShowInfoBottomSheet(val title: String, val type: GeneralInfoType) : ProfileViewTriggeredEvent()
    data object ShowLanguageBottomSheet : ProfileViewTriggeredEvent()
    data object InfoBottomSheetDismissed : ProfileViewTriggeredEvent()
    data object LanguageBottomSheetDismissed : ProfileViewTriggeredEvent()
    data class ChangeLanguage(val context: Context, val languageCode: String) : ProfileViewTriggeredEvent()
    data object Logout : ProfileViewTriggeredEvent()
    data object DeleteAccountConfirmed : ProfileViewTriggeredEvent()
    data object NavigateToLogin : ProfileViewTriggeredEvent()
    data object NavigateToLoginConsumed : ProfileViewTriggeredEvent()
    data object NavigateToSavedPhotos : ProfileViewTriggeredEvent()
    data object NavigateToSavedPhotosConsumed : ProfileViewTriggeredEvent()
    data object DismissErrorDialog : ProfileViewTriggeredEvent()
}

sealed interface GeneralInfoType {
    data object About : GeneralInfoType
    data object PrivacyPolicy : GeneralInfoType
    data object TermsAndConditions : GeneralInfoType
}