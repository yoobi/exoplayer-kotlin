package io.github.yoobi.buildlogic

import com.android.build.api.dsl.CommonExtension
import io.github.yoobi.buildlogic.extension.libCompileSdk
import io.github.yoobi.buildlogic.extension.libMinSdk
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, * ,* ,*>,
) {
    commonExtension.apply {
        compileSdk =  libCompileSdk

        defaultConfig {
            minSdk = libMinSdk
        }

        buildFeatures {
            viewBinding = true
            buildConfig = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}
