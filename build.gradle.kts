buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.41")

        // Google / Firebase
        classpath("com.google.gms:google-services:4.3.13")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.1")

        // Dependency Versions Plugin
        classpath("com.github.ben-manes:gradle-versions-plugin:0.42.0")

    }
}