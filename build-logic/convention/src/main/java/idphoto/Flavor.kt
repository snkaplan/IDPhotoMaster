@file:Suppress("UnstableApiUsage")

package idphoto

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

enum class FlavorDimension {
    CONTENT_TYPE
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
enum class Flavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    DEV(FlavorDimension.CONTENT_TYPE, ".dev"),
    PROD(FlavorDimension.CONTENT_TYPE)
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    val appName = "IDPhotoMaster"
    commonExtension.apply {
        flavorDimensions += FlavorDimension.CONTENT_TYPE.name
        productFlavors {
            Flavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                            versionNameSuffix = it.applicationIdSuffix
                            resValue(
                                "string",
                                "app_name",
                                appName + "${applicationIdSuffix?.removePrefix(".")?.uppercaseFirstChar()}"
                            )
                        } else {
                            resValue("string", "app_name", appName)
                        }
                    }
                }
            }
        }
    }
}
