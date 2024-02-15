package com.idphoto.idphotomaster.core.data.util

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity

object GoogleSignInHelper {
    fun getGoogleSignInClient(context: Context) = Identity.getSignInClient(context)

    fun getGoogleSignInRequest() = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("247244256462-ht6svg90vcee3ksffj5eae08tmqfddh6.apps.googleusercontent.com") // Can be obtained in Google Cloud
                .setFilterByAuthorizedAccounts(false)
                .build()
        ).build()
}