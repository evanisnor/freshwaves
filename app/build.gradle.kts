plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.5.31-1.0.0"
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.evanisnor.freshwaves"
        minSdk = 27
        targetSdk = 30
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "com.evanisnor.freshwaves.runner.HiltAndroidTestRunner"
    }

    buildFeatures {
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

    kotlinOptions {
        languageVersion = "1.5"
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
    // Google/Android
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.work:work-runtime-ktx:2.6.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.38.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.38.1")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    kaptAndroidTest("androidx.hilt:hilt-compiler:1.0.0")

    // Coil
    implementation("io.coil-kt:coil:1.3.2")

    // AppAuth
    implementation("net.openid:appauth:0.10.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") {
        exclude(group = "com.squareup.moshi", module = "moshi")
    }
    androidTestImplementation("com.squareup.retrofit2:retrofit-mock:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")
    implementation("com.squareup.moshi:moshi:1.12.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")

    // Room
    val roomVersion = "2.4.0-alpha04"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")

    // Instrumentation Testing
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    debugImplementation("androidx.test:monitor:1.4.0")
    debugImplementation("androidx.fragment:fragment-testing:1.3.6")
    androidTestImplementation("androidx.work:work-testing:2.6.0")
}