package it.introini.spotifyplshuffler.manager.token

import com.google.inject.ImplementedBy
import it.introini.spotifyplshuffler.manager.token.impl.TokenManagerImpl
import java.time.Instant

@ImplementedBy(TokenManagerImpl::class)
interface TokenManager {
    fun insertToken(accessToken: String, tokenType: String, scope: String, expiresIn: Long, refreshToken: String, createdOn: Instant, userId: String): Token
    fun deleteToken(userId: String): Boolean
    fun getToken(userId: String): Token?
}