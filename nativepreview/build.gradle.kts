plugins {
    id("io.github.yoobi.app")
}

android {
    namespace = "io.github.yoobi.nativepreview"
    defaultConfig {
        applicationId = "io.github.yoobi.nativepreview"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.glide)

    implementation(libs.bundles.media3.basic)

}
