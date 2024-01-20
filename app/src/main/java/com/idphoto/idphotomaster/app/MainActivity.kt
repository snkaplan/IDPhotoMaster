package com.idphoto.idphotomaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.idphoto.idphotomaster.app.appstate.AppWrapper
import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.common.Constants.KEY_TEMP_FILE_START_PREFIX
import com.idphoto.idphotomaster.core.data.util.NetworkMonitor
import com.idphoto.idphotomaster.core.data.worker.DeleteTempFilesWorker
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
        deleteTempFiles()
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

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            deleteTempFiles()
        }
    }

    private fun deleteTempFiles() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val data: Data = Data.Builder()
            .putString(KEY_TEMP_FILE_START_PREFIX, Constants.TempFileName)
            .build()
        val uploadRequest =
            OneTimeWorkRequest.Builder(DeleteTempFilesWorker::class.java)
                .setConstraints(constraints)
                .setInputData(data)
                .build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(uploadRequest)
    }
}