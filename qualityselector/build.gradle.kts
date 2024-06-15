plugins {
    id("io.github.yoobi.app")
}

android {
    namespace = "io.github.yoobi.qualityselector"
    defaultConfig {
        applicationId = "io.github.yoobi.qualityselector"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)

    implementation(libs.bundles.media3.basic)

    

}
