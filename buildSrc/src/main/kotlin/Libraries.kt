object Libraries {

    object Kotlin {
        private const val version = "1.8.10" // Don't forget to update the version in buildSrc too !

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
    }

    object Androidx {
        private const val lifecycle_version = "2.2.0"

        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val coordinatorlayout = "androidx.coordinatorlayout:coordinatorlayout:1.1.0"
        const val material = "com.google.android.material:material:1.3.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.2.0"
        const val lifecycle = "androidx.lifecycle:lifecycle-extensions:$lifecycle_version"
        const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"

        object Ktx {
            const val core = "androidx.core:core-ktx:1.5.0"
        }
    }

    object Glide {
        const val glide= "com.github.bumptech.glide:glide:4.11.0"
    }

    object Exoplayer {
        private const val version = "2.18.4"

        const val exoplayer = "com.google.android.exoplayer:exoplayer:$version"
        const val exoplayercore = "com.google.android.exoplayer:exoplayer-core:$version"
        const val exoplayerui = "com.google.android.exoplayer:exoplayer-ui:$version"
        const val exoplayerdash = "com.google.android.exoplayer:exoplayer-dash:$version"
        const val exoplayerhls = "com.google.android.exoplayer:exoplayer-hls:$version"
        const val exoplayercast = "com.google.android.exoplayer:extension-cast:$version"
        const val exoplayercornet = "com.google.android.exoplayer:extension-cronet:$version"
        const val exoplayerokhttp = "com.google.android.exoplayer:extension-okhttp:$version"
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