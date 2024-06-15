import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "io.github.yoobi.buildlogic"

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = "io.github.yoobi.app"
            implementationClass = "AndroidAppConventionPlugin"
        }
    }
}