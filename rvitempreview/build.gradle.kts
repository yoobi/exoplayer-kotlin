plugins {
    id("io.github.yoobi.app")
}

android {
    namespace = "io.github.yoobi.rvitempreview"
    defaultConfig {
        applicationId = "io.github.yoobi.rvitempreview"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.glide)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)

}
