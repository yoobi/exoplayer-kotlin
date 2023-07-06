plugins {
    androidApp()
    kotlinAndroid()
}

android {
    setAppConfig("downloadvideo")
    useDefaultBuildTypes()
    activateJava17()
}

dependencies {

    implementation(Libraries.Kotlin.stdlib)
    implementation(Libraries.Androidx.Ktx.core)
    implementation(Libraries.Androidx.appcompat)
    implementation(Libraries.Androidx.constraintlayout)
    implementation(Libraries.Androidx.coordinatorlayout)
    implementation(Libraries.Androidx.material)
    implementation(Libraries.Androidx.recyclerview)
    implementation(Libraries.Androidx.lifecycle)
    implementation(Libraries.Androidx.viewmodel)

    implementation(Libraries.Exoplayer.exoplayer)
    implementation(Libraries.Exoplayer.exoplayercornet)

    implementation(Libraries.TestLibraries.junit)
    implementation(Libraries.AndroidTestLibraries.runner)
    implementation(Libraries.AndroidTestLibraries.Espresso.core)

}
