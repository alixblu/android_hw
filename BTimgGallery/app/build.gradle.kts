plugins {
    id("com.android.application")
    id("kotlin-android") // Đảm bảo plugin kotlin đã được áp dụng
}

android {
    namespace = "com.example.btimggallery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.btimggallery"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Cấu hình Kotlin options cho JVM target
    kotlinOptions {
        jvmTarget = "1.8"  // Hoặc "11" nếu bạn sử dụng Java 11
    }
}
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }  // Thêm JitPack repository
}
dependencies {
    // Thêm Glide vào đây
    implementation ("com.github.bumptech.glide:glide:4.15.0")  // Thêm Glide
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0")  // Đảm bảo sử dụng annotationProcessor

    implementation("com.github.Baseflow:PhotoView:2.3.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
