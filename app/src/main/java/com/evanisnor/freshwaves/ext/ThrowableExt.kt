package com.evanisnor.freshwaves.ext

import retrofit2.HttpException

fun Throwable.wrapHttpException(): Throwable =
  if (this is HttpException) {
    Exception("${response()}", this)
  } else {
    this
  }
