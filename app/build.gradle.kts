plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.scanlink"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.scanlink"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.opencv)
    // Hiếu: New Dependency
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    // Hiếu: Camera X
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // vthiet: Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // vthiet: Hilt + Jetpack Compose + ViewModel
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Quickly : Room
    implementation("androidx.room:room-runtime:2.8.0")
    ksp("androidx.room:room-compiler:2.8.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")

    implementation("androidx.work:work-runtime-ktx:2.10.3")

    implementation("com.google.dagger:hilt-android:2.57")
    ksp("com.google.dagger:hilt-compiler:2.57")

    implementation("com.google.firebase:firebase-auth:24.1.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Gson
    implementation("com.google.code.gson:gson:2.13.1")

    //hilt-work
    implementation("androidx.hilt:hilt-work:1.2.0")

    ksp("androidx.hilt:hilt-compiler:1.2.0")

}