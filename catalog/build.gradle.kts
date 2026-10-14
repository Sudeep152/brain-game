import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Doodle Kit: debug-only catalog app that shows every :designsystem component in every state (T-308, D-020).
// Not shipped to players. Run config: "catalog".
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.projectpuzzle.catalog"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.projectpuzzle.catalog"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    implementation(project(":designsystem"))
    implementation(libs.androidx.activity.compose)
}
