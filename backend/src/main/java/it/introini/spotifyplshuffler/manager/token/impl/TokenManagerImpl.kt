package it.introini.spotifyplshuffler.manager.token.impl

import com.google.inject.Inject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.vertx.core.json.JsonObject
import it.introini.spotifyplshuffler.manager.token.Token
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.spotify.SpotifyUser
import org.bson.Document
import java.time.Instant

class TokenManagerImpl @Inject constructor(mongoDatabase: MongoDatabase): TokenManager {

    val collection: MongoCollection<Document> = mongoDatabase.getCollection("tokens")

    override fun insertToken(accessToken: String, tokenType: String, scope: String, expiresIn: Long, refreshToken: String, createdOn: Instant, userId: String): Token {
        val token = Token(tokenType, accessToken, refreshToken, expiresIn, scope, createdOn, userId)
        val jsonToken = JsonObject.mapFrom(token)
        collection.insertOne(Document(jsonToken.map))
        return token
    }

    override fun deleteToken(userId: String): Boolean {
        return collection.deleteOne(Filters.eq("user_id", userId)).deletedCount == 1L
    }

    override fun getToken(userId: String): Token? {
        return collection.find(Filters.eq("user_id", userId)).limit(1).map { JsonObject(it.toMap()).mapTo(Token::class.java) }.firstOrNull()
    }

    override fun updateTokenUser(userId: String, spotifyUser: SpotifyUser) {
        collection.updateOne(Filters.eq("user_id", userId), Updates.set("spotify_user", spotifyUser.id))
    }
}