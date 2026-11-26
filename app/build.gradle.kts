import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

fun Project.irisApiBaseUrl(propertyKey: String, defaultUrl: String): String {
    val v = findProperty(propertyKey)?.toString()?.trim()
    return if (v.isNullOrEmpty()) defaultUrl else v
}

fun Project.irisApiKey(propertyKey: String = "iris.api.key"): String {
    val v = findProperty(propertyKey)?.toString()?.trim()
    return if (v.isNullOrEmpty()) "" else v
}

fun Project.loadKeystoreProperties(): Properties? {
    val file = rootProject.file("keystore.properties")
    if (!file.exists()) return null
    return Properties().apply { file.inputStream().use { load(it) } }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

val keystoreProperties = loadKeystoreProperties()

android {
    namespace = "com.iris.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.iris.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "iris_api_base_url", "https://irisapp-api.vercel.app/")
        resValue("string", "iris_environment", "dev")
        resValue("string", "iris_api_key", "")
    }

    signingConfigs {
        if (keystoreProperties != null) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            val url = irisApiBaseUrl("iris.api.baseUrl.dev", "https://irisapp-api.vercel.app/")
            resValue("string", "iris_api_base_url", url)
            resValue("string", "iris_environment", "dev")
            resValue("string", "iris_api_key", "")
        }
        create("stage") {
            dimension = "environment"
            val url = irisApiBaseUrl("iris.api.baseUrl.stage", "https://irisapp-api.vercel.app/")
            resValue("string", "iris_api_base_url", url)
            resValue("string", "iris_environment", "stage")
            resValue("string", "iris_api_key", irisApiKey())
        }
        create("prod") {
            dimension = "environment"
            val url = irisApiBaseUrl("iris.api.baseUrl.prod", "https://irisapp-api.vercel.app/")
            resValue("string", "iris_api_base_url", url)
            resValue("string", "iris_environment", "prod")
            resValue("string", "iris_api_key", irisApiKey())
        }
    }

    buildTypes {
        debug {
            resValue("bool", "iris_debug", "true")
        }
        release {
            isMinifyEnabled = false
            resValue("bool", "iris_debug", "false")
            if (keystoreProperties != null) {
                signingConfig = signingConfigs.getByName("release")
            }
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

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.animation:animation")
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation(libs.coroutines.android)
    implementation(libs.datastore.preferences)
    implementation(libs.accompanist.permissions)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.splashscreen)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.googleid)
    implementation(libs.coroutines.play.services)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
