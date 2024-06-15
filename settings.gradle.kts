pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

include(
    ":app",
    ":fullscreendialog",
    ":fullscreenlayoutparams",
    ":qualityselector",
    ":rvitempreview",
    ":nativepreview",
    ":downloadvideo",
    ":cast",
    ":drmdownloadvideo"
)
rootProject.name = "exoplayer-kotlin"

// https://issuetracker.google.com/issues/328871352
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))
