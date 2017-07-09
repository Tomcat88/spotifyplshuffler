package it.introini.spotifyplshuffler.manager.state

import com.google.inject.ImplementedBy
import it.introini.spotifyplshuffler.manager.state.impl.StateManagerImpl

@ImplementedBy(StateManagerImpl::class)
interface StateManager {
    fun addState(state: String)
    fun containsState(state: String): Boolean
    fun removeState(state: String)
}