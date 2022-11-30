package com.evanisnor.freshwaves.ext

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(@StringRes stringRes: Int, duration: Int = Snackbar.LENGTH_SHORT) =
  Snackbar.make(this, stringRes, duration).show()
