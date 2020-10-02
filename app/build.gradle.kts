plugins {
    androidApp()
    kotlinAndroid()
    kotlinAndroidExt()
}

android {
    setAppConfig()
    useDefaultBuildTypes()
    activateJava8()
}

dependencies {

    implementation(Libraries.Kotlin.stdlib)
    implementation(Libraries.Androidx.appcompat)
    implementation(Libraries.Androidx.constraintlayout)
    implementation(Libraries.Androidx.Ktx.core)

    implementation(Libraries.Exoplayer.exoplayer)

    implementation(Libraries.TestLibraries.junit)
    implementation(Libraries.AndroidTestLibraries.runner)
    implementation(Libraries.AndroidTestLibraries.Espresso.core)

}
