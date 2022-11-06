import java.time.Instant

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.6.20-1.0.5"
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.github.ben-manes.versions")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.evanisnor.freshwaves"
        minSdk = 27
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"

        manifestPlaceholders["redirectUriScheme"] = "com.evanisnor.freshwaves"

        buildConfigField("Long", "BUILD_TIMESTAMP", "${Instant.now().toEpochMilli()}L")

        testInstrumentationRunner = "com.evanisnor.freshwaves.runner.HiltAndroidTestRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
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
        languageVersion = "1.6"
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi,kotlinx.coroutines.FlowPreview"
        )
    }

    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    implementation("com.evanisnor.handyauth:client:0.1.0")

    // Google/Android
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.fragment:fragment-ktx:1.5.4")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.41")
    kapt("com.google.dagger:hilt-android-compiler:2.41")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.41")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.41")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    kaptAndroidTest("androidx.hilt:hilt-compiler:1.0.0")

    // Coil
    implementation("io.coil-kt:coil:1.4.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") {
        exclude(group = "com.squareup.moshi", module = "moshi")
    }
    androidTestImplementation("com.squareup.retrofit2:retrofit-mock:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.6")
    implementation("com.squareup.moshi:moshi:1.13.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")

    // Room
    val roomVersion = "2.4.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:29.3.1"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")

    // Leak Canary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.8.1")

    // Instrumentation Testing
    debugImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    debugImplementation("androidx.test:monitor:1.5.0")
    debugImplementation("androidx.fragment:fragment-testing:1.5.4")
    androidTestImplementation("androidx.work:work-testing:2.7.1")
}