package it.introini.spotifyplshuffler.frontend.api

import kotlin.browser.window

val BASE_API = "/shuffler/api/v1"
val LOGIN_ENDPOINT = "$BASE_API/login"

open class ShufflerClient {
    companion object: ShufflerClient()

    fun login() {
        window.location.href = LOGIN_ENDPOINT
    }
}