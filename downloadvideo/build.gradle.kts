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

    implementation(Libraries.Media3.exoplayer)
    implementation(Libraries.Media3.exoplayerhls)
    implementation(Libraries.Media3.exoplayerui)
    implementation(Libraries.Media3.exoplayercornet)

    implementation(Libraries.TestLibraries.junit)
    implementation(Libraries.AndroidTestLibraries.runner)
    implementation(Libraries.AndroidTestLibraries.Espresso.core)

}
