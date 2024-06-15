plugins {
    id("io.github.yoobi.app")
}

android {
    namespace = "io.github.yoobi.downloadvideo"
    defaultConfig {
        applicationId = "io.github.yoobi.downloadvideo"
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.bundles.media3.basic)
    implementation(libs.androidx.media3.datasource.cronet)
}
