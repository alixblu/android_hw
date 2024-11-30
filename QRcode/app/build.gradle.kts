plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
        //noinspection OldTargetApi
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Using version catalog for predefined libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.barcode.scanning.v1610)
    //noinspection UseTomlInstead
    implementation ("com.google.mlkit:barcode-scanning:17.3.0")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    // Manually defined dependency for ML Kit Barcode Scanning
    implementation(libs.barcode.scanning)

    // CameraX Dependencies for camera handling
    implementation(libs.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.camera.view)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
