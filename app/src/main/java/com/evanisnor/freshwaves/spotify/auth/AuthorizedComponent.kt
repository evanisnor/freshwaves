package com.evanisnor.freshwaves.spotify.auth

interface AuthorizedComponent {

    fun authorizedAction(withAccessToken: (String) -> Unit)

}