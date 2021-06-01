// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    dependencyUpdates()
}

buildscript {
    val kotlin_version by extra("1.5.10")
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
    }
}

allprojects {
    repositories {
        google()
        jcenter()

    }
}
 task<Delete>("clean") {
    delete(rootProject.buildDir)
}

// Allow to check dependency, to automate gradle > Tasks > Help > dependencyUpdates
// right click and execute before sync or whatever you desire
tasks.named("dependencyUpdates", com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class.java).configure {

    // optional parameters
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}