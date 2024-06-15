package io.github.yoobi.buildlogic.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.libMinSdk
    get() = libs.findVersion("minSdk").get().toString().toInt()

internal val Project.libTargetSdk
    get() = libs.findVersion("targetSdk").get().toString().toInt()

internal val Project.libCompileSdk
    get() = libs.findVersion("compileSdk").get().toString().toInt()

internal fun PluginManager.applyPlugin(project: Project, alias: String) =
    apply(project.libs.findPlugin(alias).get().get().pluginId)
