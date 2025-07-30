import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.hjkarpet.ekomas"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hjkarpet.ekomas"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${localProperties.getProperty("cloudinary_cloud_name")}\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"${localProperties.getProperty("cloudinary_api_key")}\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"${localProperties.getProperty("cloudinary_api_secret")}\"")
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

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Cloudinary
    implementation(libs.cloudinary.android)
    implementation(libs.cloudinary.android.download)
    implementation(libs.cloudinary.android.preprocess)

    // PinView
    implementation(libs.pinview)

    // Lifecycle (untuk ViewModel)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // coil
    implementation(libs.coil.kt)
//    implementation(libs.coil.compose)
//    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}