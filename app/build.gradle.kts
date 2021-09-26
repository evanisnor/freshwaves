plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.5.31-1.0.0"
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.evanisnor.freshwaves"
        minSdk = 27
        targetSdk = 30
        versionCode = 1
        versionName = "0.1.0"

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

    kotlinOptions {
        languageVersion = "1.5"
        jvmTarget = "11"
    }
}

dependencies {
    // Google/Android
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.work:work-runtime-ktx:2.6.0")

    // AppAuth
    implementation("net.openid:appauth:0.10.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Room
    val roomVersion = "2.4.0-alpha04"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")

    // Instrumentation Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}