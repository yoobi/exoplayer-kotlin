plugins {
    androidApp()
    kotlinAndroid()
    kotlinAndroidExt()
}

android {
    setAppConfig()
    useDefaultBuildTypes()
    activateJava8()
//    compileOptions {
//        targetCompatibility JavaVersion.VERSION_1_8
//    }
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

//    def kotlin_version = "1.4.10"
//
//    implementation fileTree(dir: 'libs', include: ['*.jar'])
//    implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
//    implementation 'androidx.appcompat:appcompat:1.2.0'
//    implementation 'androidx.core:core-ktx:1.3.1'
//    implementation 'androidx.constraintlayout:constraintlayout:2.0.1'
//
//    //Exoplayer library
//    implementation 'com.google.android.exoplayer:exoplayer:2.11.4'
//
//    testImplementation 'junit:junit:4.13'
//    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
//    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
}
