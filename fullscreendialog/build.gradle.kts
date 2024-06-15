plugins {
    id("io.github.yoobi.app")
}

android {
    namespace = "io.github.yoobi.fullscreendialog"
    defaultConfig {
        applicationId = "io.github.yoobi.fullscreendialog"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)

    implementation(libs.bundles.media3.basic)

    

}
