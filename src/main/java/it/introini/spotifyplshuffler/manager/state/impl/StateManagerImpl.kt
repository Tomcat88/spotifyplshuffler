package it.introini.spotifyplshuffler.manager.state.impl

import com.google.inject.Inject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import it.introini.spotifyplshuffler.manager.state.StateManager
import org.bson.Document


class StateManagerImpl @Inject constructor(mongoDatabase: MongoDatabase): StateManager {

    val collection: MongoCollection<Document> = mongoDatabase.getCollection("states")

    override fun addState(state: String) {
        collection.insertOne(Document("state", state))
    }

    override fun containsState(state: String): Boolean {
        return collection.find(Document("state", state)).limit(1).toList().isNotEmpty()
    }

    override fun removeState(state: String) {
        collection.deleteOne(Document("state", state))
    }

}