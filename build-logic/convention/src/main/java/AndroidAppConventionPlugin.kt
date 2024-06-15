import com.android.build.api.dsl.ApplicationExtension
import io.github.yoobi.buildlogic.configureKotlinAndroid
import io.github.yoobi.buildlogic.extension.applyPlugin
import io.github.yoobi.buildlogic.extension.libTargetSdk
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidAppConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                applyPlugin(target, "androidApplication")
                applyPlugin(target, "kotlinAndroid")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libTargetSdk
                defaultConfig.versionCode = 1
                defaultConfig.versionName = "1.0"
                defaultConfig.multiDexEnabled = true

                buildTypes {
                    debug {
                        isMinifyEnabled = false
                    }
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }
        }
    }
}
