import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.davidniederweis.mealier"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.davidniederweis.mealier"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val baseUrl = System.getenv("MEALIE_BASE_URL")
            ?: localProperties.getProperty("MEALIE_BASE_URL")
            ?: "https://your-default-url.com" // Fallback for development
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "VERSION_NAME", "\"${versionName}\"")
        buildConfigField("int", "VERSION_CODE", "$versionCode")
        buildConfigField("String", "LOG_LEVEL", "\"DEBUG\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "LOG_LEVEL", "\"VERBOSE\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "LOG_LEVEL", "\"ERROR\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM for version management
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Lifecycle ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Networking - Retrofit
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // Coil for image loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // DataStore for preferences
    implementation(libs.androidx.datastore.preferences)

    // Security Crypto
    implementation(libs.androidx.security.crypto)
    implementation(libs.tink.android)

    // Biometric authentication
    implementation(libs.androidx.biometric)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // pull to refresh
    implementation(libs.androidx.compose.adaptive)

    // image loading
    implementation(libs.coil.kt.coil.compose)

    // logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
