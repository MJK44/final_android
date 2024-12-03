plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tonydoumit_androidmidterm_petapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tonydoumit_androidmidterm_petapp"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Authentication for user sign-up and login
    implementation(libs.firebase.auth)

    // Firebase Realtime Database for storing and retrieving pet data
    implementation(libs.firebase.database)

    // Room Database for offline data storage
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Material Design components for UI
    implementation(libs.material.v190)

    // RecyclerView for displaying scrollable lists
    implementation(libs.androidx.recyclerview)

    // Google Sign-In for easy login
    implementation(libs.play.services.auth)

    implementation(libs.androidx.room.ktx)

    // MVVM Architecture
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.firebase.firestore.ktx)
}
apply(plugin = "com.google.gms.google-services")