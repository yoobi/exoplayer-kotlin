plugins {
    id("io.github.yoobi.app")
}

android {
    namespace = "io.github.yoobi.fullscreenlayoutparams"
    defaultConfig {
        applicationId = "io.github.yoobi.fullscreenlayoutparams"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)

    implementation(libs.bundles.media3.basic)
    implementation(libs.androidx.media3.common)

}
