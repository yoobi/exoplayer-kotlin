plugins {
    androidApp()
    kotlinAndroid()
}

android {
    setAppConfig("cast")
    useDefaultBuildTypes()
    activateJava17()
}

dependencies {

    implementation(Libraries.Kotlin.stdlib)
    implementation(Libraries.Androidx.appcompat)
    implementation(Libraries.Androidx.material)
    implementation(Libraries.Androidx.constraintlayout)
    implementation(Libraries.Androidx.Ktx.core)
    implementation(Libraries.Androidx.mediarouter)

    implementation(Libraries.Media3.exoplayer)
    implementation(Libraries.Media3.exoplayerui)
    implementation(Libraries.Media3.exoplayerhls)
    implementation(Libraries.Media3.exoplayercast)

    implementation(Libraries.TestLibraries.junit)
    implementation(Libraries.AndroidTestLibraries.runner)
    implementation(Libraries.AndroidTestLibraries.Espresso.core)

}
