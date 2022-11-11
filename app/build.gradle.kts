import java.time.Instant

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.kapt)
  alias(libs.plugins.google.ksp)
  alias(libs.plugins.dagger.hilt)
  alias(libs.plugins.google.services)
  alias(libs.plugins.google.firebase.crashlytics)
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
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
  }

  kotlinOptions {
    languageVersion =
      Regex("(\\d+\\.\\d+)\\.\\d+").find(libs.versions.kotlin.get())!!.groupValues[1]
    jvmTarget = libs.versions.jvm.get()
  }

  testOptions {
    animationsDisabled = true
  }
}

dependencies {
  implementation(libs.bundles.androidx.framework)
  implementation(libs.bundles.hilt)
  implementation(libs.bundles.square.retrofit) {
    exclude(group = "com.squareup.moshi", module = "moshi")
  }
  implementation(libs.coil)
  implementation(platform(libs.google.firebase.bom))
  implementation(libs.google.firebase.analytics)
  implementation(libs.google.firebase.crashlytics)
  implementation(libs.google.material)
  implementation(libs.handyauth.client)
  implementation(libs.jetbrains.coroutines.android)
  implementation(libs.square.okhttp.logging)
  implementation(libs.square.moshi)
  ksp(libs.androidx.room.compiler)
  kapt(libs.bundles.hilt.compiler)
  ksp(libs.square.moshi.kotlin.codegen)

  debugImplementation(libs.bundles.androidx.debug)
  debugImplementation(libs.square.leakcanary.android)

  testImplementation(libs.google.test.truth)
  testImplementation(libs.jetbrains.test.coroutines)
  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.turbine)

  androidTestImplementation(libs.bundles.androidx.instrumentationtest)
  androidTestImplementation(libs.google.test.dagger.hilt.android)
  androidTestImplementation(libs.jetbrains.test.coroutines)
  androidTestImplementation(libs.square.test.retrofit.mock)
  kaptAndroidTest(libs.bundles.hilt.compiler)
}