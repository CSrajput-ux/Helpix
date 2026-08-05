import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.isFile) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun releaseProperty(name: String): String? =
    providers.gradleProperty(name).orNull ?: localProperties.getProperty(name)

val productionApiUrl = releaseProperty("productionApiUrl")
val googleMapsApiKey = releaseProperty("googleMapsApiKey")
val releaseStoreFile = releaseProperty("releaseStoreFile")
val releaseStorePassword = releaseProperty("releaseStorePassword")
val releaseKeyAlias = releaseProperty("releaseKeyAlias")
val releaseKeyPassword = releaseProperty("releaseKeyPassword")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.healthai.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.healthai.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["googleMapsApiKey"] = googleMapsApiKey.orEmpty()
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            if (!releaseStoreFile.isNullOrBlank()) {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000/\"")
            manifestPlaceholders["cleartextTrafficPermitted"] = "true"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "API_BASE_URL", "\"${productionApiUrl.orEmpty()}\"")
            manifestPlaceholders["cleartextTrafficPermitted"] = "false"
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        jniLibs {
            useLegacyPackaging = true
        }
    }
    
    androidResources {
        noCompress += "tflite"
    }

}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

tasks.register("validateReleaseConfiguration") {
    inputs.properties(
        mapOf(
            "productionApiUrl" to productionApiUrl.orEmpty(),
            "googleMapsApiKey" to googleMapsApiKey.orEmpty(),
            "releaseStoreFile" to releaseStoreFile.orEmpty(),
            "releaseStorePassword" to releaseStorePassword.orEmpty(),
            "releaseKeyAlias" to releaseKeyAlias.orEmpty(),
            "releaseKeyPassword" to releaseKeyPassword.orEmpty(),
        )
    )
    doLast {
        val missing = inputs.properties
            .filterValues { it.toString().isBlank() }
            .keys
            .sorted()
        check(missing.isEmpty()) {
            "Release configuration is incomplete. Set: ${missing.joinToString(", ")}"
        }
    }
}

tasks.configureEach {
    if (name == "preReleaseBuild") {
        dependsOn("validateReleaseConfiguration")
    }
}

dependencies {
    // Core Android & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.guava)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.appcompat)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-ml-modeldownloader")

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation("androidx.camera:camera-mlkit-vision:1.4.1")

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)

    // ML Kit
    implementation(libs.google.mlkit.vision.common)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Biometric & Health
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.health.connect)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Google Maps, Places, Location, and Authentication
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.play.services.auth)
    implementation(libs.google.places)
    
    // Image Loading
    implementation(libs.coil.compose)

    // Testing & Debugging
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
