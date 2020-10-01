import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpec.androidApp(): PluginDependencySpec =
    id("com.android.application")

fun PluginDependenciesSpec.kotlinAndroid(): PluginDependencySpec =
    kotlin("android")

fun PluginDependenciesSpec.kotlinAndroidExt(): PluginDependencySpec =
    kotlin("android.extensions")
