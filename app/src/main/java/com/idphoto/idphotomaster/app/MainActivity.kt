package com.idphoto.idphotomaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.idphoto.idphotomaster.app.appstate.AppWrapper
import com.idphoto.idphotomaster.core.data.util.NetworkMonitor
import com.idphoto.idphotomaster.core.systemdesign.components.CustomDialog
import com.idphoto.idphotomaster.core.systemdesign.components.DialogItem
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.IDPhotoMasterTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dialogItem = remember { mutableStateOf<DialogItem?>(null) }
            LaunchedEffect(key1 = viewModel.uiEvents) {
                viewModel.uiEvents.collect { event ->
                    when (event) {
                        is MainViewEvents.ShowCustomDialog -> {
                            dialogItem.value = DialogItem(
                                event.title,
                                event.message,
                                event.confirmText,
                                event.dismissText
                            )
                        }
                    }
                }
            }
            IDPhotoMasterTheme {
                if (dialogItem.value != null) {
                    CustomDialog(dialogItem = dialogItem)
                }
                AppWrapper(networkMonitor = networkMonitor, mainViewModel = viewModel)
            }
        }
    }
}