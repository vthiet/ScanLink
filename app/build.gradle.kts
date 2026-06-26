import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

ksp {
    arg("room.generateKotlin", "true")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val devBaseUrl = localProperties.getProperty("BASE_URL") ?: "http://10.0.2.2"

android {
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"$devBaseUrl\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "BASE_URL", "\"https://api.yourdomain.com\"")
        }
    }

    namespace = "com.example.scanlink"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.scanlink"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // =========================================================
    // CORE ANDROID & KOTLIN
    // =========================================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.play.services)

    // =========================================================
    // JETPACK COMPOSE
    // =========================================================
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // =========================================================
    // NAVIGATION
    // =========================================================
    implementation(libs.androidx.navigation.compose)

    // =========================================================
    // VIEWMODEL
    // =========================================================
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // =========================================================
    // HILT (DEPENDENCY INJECTION)
    // =========================================================
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.hilt.navigation.compose)

    // =========================================================
    // FIREBASE
    // =========================================================
    implementation(libs.firebase.auth.ktx.v2231)
    implementation(libs.firebase.analytics.ktx)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // =========================================================
    // NETWORKING
    // =========================================================
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)

    // =========================================================
    // CAMERA & QR SCANNING
    // =========================================================
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    
    // ExifInterface
    implementation(libs.androidx.exifinterface)

    // =========================================================
    // ML KIT
    // =========================================================
    implementation(libs.mlkit.text.recognition)

    implementation(libs.opencv)

    // =========================================================
    // ROOM DATABASE
    // =========================================================
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // =========================================================
    // UI COMPONENTS
    // =========================================================
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)

    // =========================================================
    // UNIT TEST
    // =========================================================
    testImplementation(libs.junit)

    // =========================================================
    // INSTRUMENTATION TEST
    // =========================================================
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // =========================================================
    // DEBUG TOOLS
    // =========================================================
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}