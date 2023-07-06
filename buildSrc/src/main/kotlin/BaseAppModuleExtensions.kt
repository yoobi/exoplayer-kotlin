import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion

fun BaseAppModuleExtension.setAppConfig(module: String = "exoplayer") {
    namespace = "io.github.yoobi.$module"
    compileSdk = ProjectConfiguration.compileSdk
    buildToolsVersion = ProjectConfiguration.buildTools
    defaultConfig {
        minSdk = ProjectConfiguration.minSdk
        targetSdk = ProjectConfiguration.targetSdk

        applicationId = ProjectConfiguration.applicationId.plus(".$module")
        versionCode = ProjectConfiguration.versionCode
        versionName = ProjectConfiguration.versionName
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        testInstrumentationRunner = ProjectConfiguration.testInstrumentationRunner
    }
}

fun BaseExtension.setDefaultConfig() {
    compileSdkVersion(ProjectConfiguration.compileSdk)
    buildToolsVersion(ProjectConfiguration.buildTools)

    defaultConfig {
        minSdk = ProjectConfiguration.minSdk
        targetSdk = ProjectConfiguration.targetSdk
        multiDexEnabled = true

        testInstrumentationRunner = ProjectConfiguration.testInstrumentationRunner
    }
}

fun BaseExtension.useDefaultBuildTypes() = buildTypes {
    getByName("release") {
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    getByName("debug") {
        isMinifyEnabled = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}

fun BaseExtension.activateJava17() = compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
