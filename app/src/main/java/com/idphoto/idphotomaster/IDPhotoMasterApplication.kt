package com.idphoto.idphotomaster

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.idphoto.idphotomaster.core.data.datasource.local.LocalDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class IDPhotoMasterApplication : Application() {

    @Inject
    lateinit var localDataStore: LocalDataStore

    override fun onCreate() {
        setAppLocale(this)
        super.onCreate()
    }

    private fun setAppLocale(context: Context) {
        MainScope().launch {
            if (localDataStore.isAppOpenedBefore().first().not()) {
                val locale = if (Locale.getDefault().language == Locale("tr").language) {
                    Locale("tr")
                } else {
                    Locale("en")
                }

                Locale.setDefault(locale)
                val config = Configuration()
                config.setLocale(locale)
                context.createConfigurationContext(config)
                localDataStore.setIsAppOpenedBefore(true)
            }
            return@launch
        }
    }
}