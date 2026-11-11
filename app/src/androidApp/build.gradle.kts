import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "nl.hexmaster.final_approach"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "nl.hexmaster.final_approach"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = System.getenv("VERSION_CODE")?.toInt() ?: 1
        versionName = System.getenv("VERSION_NAME") ?: "0.1.0"
    }
    // Release signing comes from the environment (CI); see docs/deployment.md.
    val uploadKeystorePath = System.getenv("UPLOAD_KEYSTORE_PATH")
    signingConfigs {
        if (uploadKeystorePath != null) {
            create("release") {
                storeFile = file(uploadKeystorePath)
                storePassword = System.getenv("UPLOAD_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("UPLOAD_KEY_ALIAS")
                keyPassword = System.getenv("UPLOAD_KEY_PASSWORD")
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
    // Debug builds only ever use Google's public test ads; the real units are for release.
    buildTypes {
        getByName("debug") {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "INTERSTITIAL_UNIT_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "BANNER_UNIT_ID", "\"ca-app-pub-3940256099942544/9214589741\"")
        }
        getByName("release") {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-1310013076234295~7766080345"
            buildConfigField("String", "INTERSTITIAL_UNIT_ID", "\"ca-app-pub-1310013076234295/6684168624\"")
            buildConfigField("String", "BANNER_UNIT_ID", "\"ca-app-pub-1310013076234295/3427449894\"")
            isMinifyEnabled = false
            if (uploadKeystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)
}
