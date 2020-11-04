import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion

fun BaseAppModuleExtension.setAppConfig() {
    compileSdkVersion(ProjectConfiguration.compileSdk)
    buildToolsVersion(ProjectConfiguration.buildTools)

    defaultConfig {
        minSdkVersion(ProjectConfiguration.minSdk)
        targetSdkVersion(ProjectConfiguration.targetSdk)

        applicationId = ProjectConfiguration.applicationId
        versionCode = ProjectConfiguration.versionCode
        versionName = ProjectConfiguration.versionName
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = ProjectConfiguration.testInstrumentationRunner
    }
}

fun BaseExtension.setDefaultConfig() {
    compileSdkVersion(ProjectConfiguration.compileSdk)
    buildToolsVersion(ProjectConfiguration.buildTools)

    defaultConfig {
        minSdkVersion(ProjectConfiguration.minSdk)
        targetSdkVersion(ProjectConfiguration.targetSdk)

        testInstrumentationRunner = ProjectConfiguration.testInstrumentationRunner
    }
}

fun BaseExtension.useDefaultBuildTypes() = buildTypes {
    getByName("release") {
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    getByName("debug") {
        isMinifyEnabled = true
        isUseProguard = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}

fun BaseExtension.activateJava8() = compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
