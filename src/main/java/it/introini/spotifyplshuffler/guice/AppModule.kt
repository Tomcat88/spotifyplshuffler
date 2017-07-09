package it.introini.spotifyplshuffler.guice

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import it.introini.spotifyplshuffler.config.Config
import it.introini.spotifyplshuffler.config.Parameter


class AppModule : AbstractModule() {
    override fun configure() { /* NOP */ }

    @Provides @Singleton fun vertx() = Vertx.currentContext().owner()
    @Provides @Singleton @Inject fun router(vertx: Vertx) = Router.router(vertx)

    @Singleton @Provides @Inject fun mongoClient(config: Config): MongoClient {
        val host = config.getString(Parameter.MONGO_DB_HOST)
        val port = config.getInt(Parameter.MONGO_DB_PORT)
        return MongoClient(host, port)
    }

    @Singleton @Provides @Inject fun mongoDb(config: Config, mongoClient: MongoClient): MongoDatabase {
        return mongoClient.getDatabase(config.getString(Parameter.MONGO_DB_NAME))
    }


}