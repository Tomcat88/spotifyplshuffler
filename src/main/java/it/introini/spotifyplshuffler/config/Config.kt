package it.introini.spotifyplshuffler.config

import com.google.inject.Inject
import io.vertx.core.json.JsonObject

class Config @Inject constructor(val jsonConfig: JsonObject) {

    fun getString(parameter: Parameter)  = jsonConfig.getString(parameter.parameter, parameter.default.toString())
    fun getInt(parameter: Parameter)     = jsonConfig.getInteger(parameter.parameter, parameter.default.toString().toInt())
    fun getDouble(parameter: Parameter)  = jsonConfig.getDouble(parameter.parameter, parameter.default.toString().toDouble())
    fun getLong(parameter: Parameter)    = jsonConfig.getLong(parameter.parameter, parameter.default.toString().toLong())
    fun getBoolean(parameter: Parameter) = jsonConfig.getBoolean(parameter.parameter, parameter.default.toString().toBoolean())
}