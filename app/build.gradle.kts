@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.kapt)
  alias(libs.plugins.google.ksp)
  alias(libs.plugins.dagger.hilt)
  alias(libs.plugins.google.services)
  alias(libs.plugins.google.firebase.crashlytics)
  alias(libs.plugins.spotless)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")

android {
  compileSdk = 33

  defaultConfig {
    applicationId = "com.evanisnor.freshwaves"
    minSdk = 27
    targetSdk = 33
    versionCode = countGitCommits().also {
      println("Current Version Code: $it")
    }
    versionName = generateVersionNumber().also {
      println("Current Version: $it")
    }

    manifestPlaceholders["adMobAppId"] = readEnvironmentVariable(
      name = "ADMOB_APPID",
      default = "ca-app-pub-3940256099942544~3347511713",
    )
    manifestPlaceholders["adMobAdAlbumCard"] = readEnvironmentVariable(
      name = "ADMOB_AD_ALBUMCARD",
      default = "ca-app-pub-3940256099942544/2247696110",
    )
    manifestPlaceholders["redirectUriScheme"] = "com.evanisnor.freshwaves"

    buildConfigField("Long", "BUILD_TIMESTAMP", "${Instant.now().toEpochMilli()}L")

    testInstrumentationRunner = "com.evanisnor.freshwaves.runner.HiltAndroidTestRunner"
  }

  if (keystorePropertiesFile.exists()) {
    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    signingConfigs {
      create("release") {
        keyAlias = keystoreProperties["keyAlias"].toString()
        keyPassword = keystoreProperties["keyPassword"].toString()
        storeFile = file(keystoreProperties["storeFile"].toString())
        storePassword = keystoreProperties["storePassword"].toString()
      }
    }
  }

  buildFeatures {
    buildConfig = true
    viewBinding = true
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      if (keystorePropertiesFile.exists()) {
        signingConfig = signingConfigs.getByName("release")
      }
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
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
    unitTests.isIncludeAndroidResources = true
  }
}

spotless {
  val editorConfig = mapOf(
    "indent_size" to 2,
    "no-wildcard-imports" to true,
    "ij_kotlin_allow_trailing_comma" to true,
    "ij_kotlin_allow_trailing_comma_on_call_site" to true,
  )

  kotlin {
    ktlint(libs.versions.ktlint.get())
      .setUseExperimental(true)
      .editorConfigOverride(editorConfig)
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktlint(libs.versions.ktlint.get())
      .editorConfigOverride(editorConfig)
  }
}

dependencies {
  implementation(libs.bundles.androidx.framework)
  implementation(libs.bundles.hilt)
  implementation(libs.bundles.square.retrofit) {
    exclude(group = "com.squareup.moshi", module = "moshi")
  }
  implementation(libs.coil)
  implementation(libs.google.ads)
  implementation(platform(libs.google.firebase.bom))
  implementation(libs.google.firebase.analytics)
  implementation(libs.google.firebase.crashlytics)
  implementation(libs.google.material)
  implementation(libs.handyauth.client)
  implementation(libs.jetbrains.coroutines.android)
  implementation(libs.square.moshi)
  implementation(libs.timber)
  ksp(libs.androidx.room.compiler)
  kapt(libs.bundles.hilt.compiler)
  ksp(libs.square.moshi.kotlin.codegen)

  debugImplementation(libs.square.leakcanary.android)
  debugImplementation(libs.square.okhttp.logging)

  implementation(libs.androidx.test.fragment)
  testImplementation(libs.bundles.androidx.debug)
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

/**
 * Handy function for executing shell commands and getting the output
 */
fun String.execute(vararg args: String = emptyArray()): String {
  val outputStream = ByteArrayOutputStream()
  project.exec {
    workingDir = projectDir
    environment("TZ", "Etc/UTC")
    commandLine(mutableListOf(this@execute).apply { addAll(args) })
    standardOutput = outputStream
  }
  return String(outputStream.toByteArray()).trim()
}

fun generateVersionNumber(): String {
  val year = "date".execute("+\"%Y\"").trim('"')
  val month = "date".execute("+\"%m\"").trim('"')
  val commitsThisMonth = countGitCommits(since = "$year-$month-01 00:00:00")
  return "$year.$month.$commitsThisMonth"
}

fun countGitCommits(since: String? = null) = if (!since.isNullOrBlank()) {
  "git".execute("rev-list", "--count", "main", "--since=\"$since\"")
} else {
  "git".execute("rev-list", "--count", "main")
}.toInt()

fun readEnvironmentVariable(
  name: String,
  default: String,
): String {
  val fromEnv = System.getenv(name.toUpperCaseAsciiOnly())
  return if (fromEnv.isNullOrBlank()) default else fromEnv
}
