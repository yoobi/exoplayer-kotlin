plugins {
    androidApp()
    kotlinAndroid()
}

android {
    setAppConfig("fullscreenlayoutparams")
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
    implementation(Libraries.Media3.exoplayercommon)
    implementation(Libraries.Media3.exoplayerhls)

    implementation(Libraries.TestLibraries.junit)
    implementation(Libraries.AndroidTestLibraries.runner)
    implementation(Libraries.AndroidTestLibraries.Espresso.core)

}
