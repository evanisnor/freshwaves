package com.evanisnor.freshwaves.system

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String) =
  ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
