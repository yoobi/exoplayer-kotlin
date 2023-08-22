plugins {
    androidApp()
    kotlinAndroid()
}

android {
    setAppConfig()
    useDefaultBuildTypes()
    activateJava17()
}

dependencies {

    implementation(Libraries.Kotlin.stdlib)
    implementation(Libraries.Androidx.appcompat)
    implementation(Libraries.Androidx.constraintlayout)
    implementation(Libraries.Androidx.Ktx.core)

    implementation(Libraries.Media3.exoplayer)
    implementation(Libraries.Media3.exoplayerui)

    implementation(Libraries.TestLibraries.junit)
    implementation(Libraries.AndroidTestLibraries.runner)
    implementation(Libraries.AndroidTestLibraries.Espresso.core)
    implementation("androidx.media3:media3-ui:1.1.1")

}
