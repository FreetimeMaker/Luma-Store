plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
}

android {
    namespace = "com.freetime.appstore"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.freetime.lumastore"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val lumaApiBaseUrl = (project.findProperty("LUMA_API_BASE_URL") as String?)
            ?: "https://all-api-node.vercel.app"
        buildConfigField("String", "LUMA_API_BASE_URL", "\"${lumaApiBaseUrl.trimEnd('/')}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Supabase
    implementation("io.github.supabase-community:supabase-kt:2.0.0") {
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    implementation("io.ktor:ktor-client-android:2.3.8")
    implementation("io.ktor:ktor-client-serialization:2.3.8")

    // Jetpack
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
