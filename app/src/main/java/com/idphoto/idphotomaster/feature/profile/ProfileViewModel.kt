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
import com.idphoto.idphotomaster.core.domain.usecase.config.GetConfigUseCase
import com.idphoto.idphotomaster.core.domain.usecase.profile.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getConfigUseCase: GetConfigUseCase
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

    private fun getConfig() {
        viewModelScope.launch {
            getConfigUseCase.invoke().asResource().onEach { result ->
                when (result) {
                    is Resource.Error -> {}
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
                        updateState { copy(loading = false) }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false, user = result.data, loggedIn = true) }
                    }
                }
            }.launchIn(this)
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
            }
        }
    }

    private fun changeLanguage(context: Context, code: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags(code)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
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
    val config: FirebaseRemoteConfig? = null
) : IViewState

sealed class ProfileViewTriggeredEvent {
    data class ShowInfoBottomSheet(val title: String, val type: GeneralInfoType) : ProfileViewTriggeredEvent()
    data object ShowLanguageBottomSheet : ProfileViewTriggeredEvent()
    data object InfoBottomSheetDismissed : ProfileViewTriggeredEvent()
    data object LanguageBottomSheetDismissed : ProfileViewTriggeredEvent()
    data class ChangeLanguage(val context: Context, val languageCode: String) : ProfileViewTriggeredEvent()
}

sealed interface GeneralInfoType {
    data object About : GeneralInfoType
    data object PrivacyPolicy : GeneralInfoType
    data object TermsAndConditions : GeneralInfoType
}