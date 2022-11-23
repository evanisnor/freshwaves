package com.evanisnor.freshwaves

import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest
import java.util.Base64

/**
 * Return the app's certificate signature as a base64-encoded string. We can use this to
 * safely identify the app at runtime. This value should be kept SECRET and only sent over secure
 * channels (ie: TLS)
 */
fun PackageManager.getAppSignature(packageName: String): String {
  val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getPackageInfo(
      packageName,
      PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong()),
    ).signingInfo.signingCertificateHistory.first().toByteArray()
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    @Suppress("DEPRECATION")
    getPackageInfo(
      packageName,
      PackageManager.GET_SIGNING_CERTIFICATES,
    ).signatures.first().toByteArray()
  } else {
    @Suppress("PackageManagerGetSignatures", "DEPRECATION")
    getPackageInfo(
      packageName,
      PackageManager.GET_SIGNATURES,
    ).signatures.first().toByteArray()
  }

  val hash = MessageDigest.getInstance("SHA-256").digest(signature)
  return Base64.getEncoder().encodeToString(hash)
}
