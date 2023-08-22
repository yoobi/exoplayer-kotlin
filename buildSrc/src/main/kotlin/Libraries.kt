object Libraries {

    object Kotlin {
        // Don't forget to update the version in:
        // root/buildSrc/build.gradle.kts
        // root/build.gradle.kts
        private const val version = "1.8.10"

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
    }

    object Androidx {
        private const val lifecycle_version = "2.2.0"

        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val coordinatorlayout = "androidx.coordinatorlayout:coordinatorlayout:1.1.0"
        const val material = "com.google.android.material:material:1.3.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.2.0"
        const val mediarouter = "androidx.mediarouter:mediarouter:1.4.0"
        const val lifecycle = "androidx.lifecycle:lifecycle-extensions:$lifecycle_version"
        const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"

        object Ktx {
            const val core = "androidx.core:core-ktx:1.5.0"
        }
    }

    object Glide {
        const val glide= "com.github.bumptech.glide:glide:4.11.0"
    }

    object Media3 {
        private const val version = "1.1.1"

        const val exoplayer = "androidx.media3:media3-exoplayer:$version"
        const val exoplayerui = "androidx.media3:media3-ui:$version"
        const val exoplayercommon = "androidx.media3:media3-common:$version"
        const val exoplayerdash = "androidx.media3:media3-exoplayer-dash:$version"
        const val exoplayerhls = "androidx.media3:media3-exoplayer-hls:$version"
        const val exoplayercast = "androidx.media3:media3-cast:$version"
        const val exoplayercornet = "androidx.media3:media3-datasource-cronet:$version"
    }

    object TestLibraries {
        const val junit = "junit:junit:4.13.2"
    }

    object AndroidTestLibraries {
        const val runner = "androidx.test.ext:junit-ktx:1.1.5"

        object Espresso {
            private const val version = "3.5.1"

            const val core = "androidx.test.espresso:espresso-core:$version"
        }
    }

}