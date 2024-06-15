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
