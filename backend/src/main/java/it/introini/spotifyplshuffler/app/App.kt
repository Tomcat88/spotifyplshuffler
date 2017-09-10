package it.introini.spotifyplshuffler.app

import com.google.inject.Inject
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import it.introini.spotifyplshuffler.config.Config
import it.introini.spotifyplshuffler.config.Parameter
import it.introini.spotifyplshuffler.routes.RouteBinder
import org.pmw.tinylog.Logger

class App @Inject constructor(private val vertx: Vertx,
                              private val router: Router,
                              private val routeBinder: RouteBinder,
                              private val config: Config) {

    fun startup(future: Future<Void>) {
        startWebServer().setHandler {
            if (it.succeeded()) {
                Logger.info("Startup complete!")
                future.complete()
            } else {
                Logger.info("Something went wrong!", it.cause())
                future.fail(it.cause())
            }
        }
    }

    private fun startWebServer(): Future<Void> {
        val future = Future.future<Void>()
        val port = config.getInt(Parameter.HTTP_PORT)
        val httpServer = vertx.createHttpServer()
        httpServer.requestHandler{ router.accept(it) }
        httpServer.listen(port) {
            if (it.succeeded()) {
                routeBinder.bindRoutes()
                Logger.info("Http server up! listening on port $port")
                future.complete()
            } else {
                Logger.error("Something went wrong while starting http server: ${it.cause().message}",it.cause())
                future.fail(it.cause())
            }
        }
        return future
    }
}