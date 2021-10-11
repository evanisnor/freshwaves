package com.evanisnor.freshwaves.spotify.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.enqueue(
    onResult: (T) -> Unit,
    onError: (Throwable) -> Unit
) = enqueue(object : Callback<T> {
    override fun onResponse(
        call: Call<T>,
        response: Response<T>
    ) {
        if (!response.isSuccessful) {
            onFailure(
                call,
                Exception(
                    "Received error code ${response.code()}\n${
                        call.request().url
                    }\n${response.errorBody()!!.string()}"
                )
            )
        } else if (response.body() == null) {
            onFailure(
                call,
                Exception("Failed to parse response body")
            )
        } else {
            response.body()?.let(onResult)
        }
    }

    override fun onFailure(
        call: Call<T>,
        t: Throwable
    ) {
        onError(t)
    }
})