pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version "8.7.2" apply false
        id("com.android.library") version "8.7.2" apply false
        kotlin("android") version "1.9.0" apply false
        kotlin("jvm") version "1.9.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "BTimgGallery"
include(":app")
