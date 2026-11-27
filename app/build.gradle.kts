import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

// Default: BE yang sudah dideploy (HTTPS), jadi HP tidak perlu tersambung USB.
// Untuk BE lokal, isi local.properties: api.base.url=http://localhost:8000/api/v1/ (adb reverse)
// atau http://10.0.2.2:8000/api/v1/ (emulator).
val apiBaseUrl: String = localProperties.getProperty("api.base.url") ?: "https://temukarya.bccdev.id/api/v1/"

// Kunci tanda tangan APK release, dibaca dari keystore.properties di root proyek (tidak di-commit).
val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

/**
 * Konfigurasi Firebase dari app/google-services.json (Firebase Console → Project settings → Your apps → Android).
 * Dibaca langsung tanpa plugin google-services; bila file belum ada, build tetap jalan dan push notifikasi nonaktif.
 */
data class FirebaseConfig(val appId: String, val apiKey: String, val projectId: String, val senderId: String)

val firebaseConfig: FirebaseConfig? = run {
    val file = project.file("google-services.json")
    if (!file.exists()) return@run null
    @Suppress("UNCHECKED_CAST")
    val json = groovy.json.JsonSlurper().parse(file) as Map<String, Any?>
    val projectInfo = json["project_info"] as Map<String, Any?>
    val clients = json["client"] as List<Map<String, Any?>>
    val client = clients.firstOrNull {
        val info = (it["client_info"] as Map<String, Any?>)["android_client_info"] as Map<String, Any?>
        info["package_name"] == "com.example.proyek_papb_fe"
    } ?: error("google-services.json tidak berisi app Android dengan package com.example.proyek_papb_fe")
    FirebaseConfig(
        appId = (client["client_info"] as Map<String, Any?>)["mobilesdk_app_id"] as String,
        apiKey = ((client["api_key"] as List<Map<String, Any?>>).first())["current_key"] as String,
        projectId = projectInfo["project_id"] as String,
        senderId = projectInfo["project_number"] as String
    )
}

android {
    namespace = "com.example.proyek_papb_fe"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.proyek_papb_fe"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "FIREBASE_APP_ID", "\"${firebaseConfig?.appId.orEmpty()}\"")
        buildConfigField("String", "FIREBASE_API_KEY", "\"${firebaseConfig?.apiKey.orEmpty()}\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"${firebaseConfig?.projectId.orEmpty()}\"")
        buildConfigField("String", "FIREBASE_SENDER_ID", "\"${firebaseConfig?.senderId.orEmpty()}\"")
    }

    signingConfigs {
        if (keystoreProperties.getProperty("storeFile") != null) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            optimization {
                enable = false
            }
        }
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// Hanya bila memakai BE lokal (localhost): port 8000 laptop diteruskan ke HP setiap kali build debug.
// Gagal diam-diam bila HP tidak tersambung, supaya build tetap jalan.
val adbReverse by tasks.registering(Exec::class) {
    val sdkDir = localProperties.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME").orEmpty()
    executable = "$sdkDir/platform-tools/adb"
    args("reverse", "tcp:8000", "tcp:8000")
    isIgnoreExitValue = true
}

if (apiBaseUrl.contains("localhost")) {
    tasks.matching { it.name == "assembleDebug" }.configureEach {
        finalizedBy(adbReverse)
    }
}
