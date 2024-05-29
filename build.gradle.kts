buildscript {
    val kotlinVersion by extra("1.8.22")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:4.3.10")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
