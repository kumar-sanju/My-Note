pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "7.1.0"
        id("org.jetbrains.kotlin.android") version "1.8.22"
        id("com.google.gms.google-services") version "4.3.10" // Add the correct version here
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "My Note"
include(":app")
 