package it.introini.spotifyplshuffler.app

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.inject.Guice
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.Json
import it.introini.spotifyplshuffler.config.Config
import it.introini.spotifyplshuffler.config.Parameter
import it.introini.spotifyplshuffler.guice.AppModule
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import org.pmw.tinylog.writers.ConsoleWriter
import org.pmw.tinylog.writers.FileWriter


class AppVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {
        Json.mapper.registerModule(Jdk8Module())
        Json.mapper.registerModule(JavaTimeModule())
        Json.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        val injector = Guice.createInjector(AppModule())
        val config   = injector.getInstance(Config::class.java)
        val app      = injector.getInstance(App::class.java)

        logInit(config)
        app.startup(startFuture)
    }

    private fun logInit(config: Config) {
        Configurator.currentConfig()
                .writer(FileWriter(config.getString(Parameter.LOG_FILE)))
                .writer(ConsoleWriter())
                .formatPattern("[{date}] {level}: {class}.{method}()\t {message}")
                .level(Level.valueOf(config.getString(Parameter.LOG_LEVEL)))
                .activate()
    }

}