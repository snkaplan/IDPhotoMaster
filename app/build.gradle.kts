plugins {
    alias(libs.plugins.idphoto.android.application)
    alias(libs.plugins.idphoto.android.application.compose)
    alias(libs.plugins.idphoto.android.application.flavors)
    alias(libs.plugins.idphoto.android.application.jacoco)
    alias(libs.plugins.idphoto.android.hilt)
    id("jacoco")
    id("idphoto.kotlinter")
}

android {
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.idphoto.idphotomaster"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    namespace = "com.idphoto.idphotomaster"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.metrics.performance)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)

    //Chucker
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)

    implementation(libs.androidx.material.icons.extended)

    //Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth.ktx) // No Version needed because the BOM manages that
    implementation(libs.play.services.auth)

    // Add the dependencies for the Crashlytics libraries
    implementation(libs.firebase.crashlytics)

    // Add the dependencies for the Remote Config and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    //// ACCOMPANIST ////
    implementation(libs.accompanist.permissions)
    implementation(libs.gpuimage)

    implementation(libs.play.services.mlkit.subject.segmentation)

    implementation(libs.androidx.work.runtime)

    implementation(libs.billing.ktx)

    implementation(libs.coil.compose)

    implementation(libs.material3)

    implementation(libs.compose.state.events)
}