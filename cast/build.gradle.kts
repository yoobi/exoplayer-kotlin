plugins {
    id("io.github.yoobi.app")
}

android {
    namespace = "io.github.yoobi.cast"
    defaultConfig {
        applicationId = "io.github.yoobi.cast"
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.mediarouter)

    implementation(libs.bundles.media3.basic)
    implementation(libs.androidx.media3.cast)
}
