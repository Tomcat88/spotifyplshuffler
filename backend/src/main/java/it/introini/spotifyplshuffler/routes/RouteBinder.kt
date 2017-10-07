package it.introini.spotifyplshuffler.routes

import com.google.inject.Inject
import com.google.inject.Injector
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.LoggerHandler
import it.introini.spotifyplshuffler.config.Config
import it.introini.spotifyplshuffler.handlers.ApplicationPageHandler
import org.pmw.tinylog.Logger


class RouteBinder @Inject constructor( private val injector: Injector,
                                       private val router:   Router,
                                               val vertx:    Vertx ) {

    private val apiRoot = "/shuffler/api/v1"

    fun bindRoutes() {
        val routes = Route.values()
        router.route("$apiRoot/*").handler(BodyHandler.create())
        router.route("$apiRoot/*").handler(LoggerHandler.create())
        router.route("$apiRoot/*").handler(CookieHandler.create())
        routes.forEach {
            router.route(it.method, "$apiRoot${it.endpoint}").blockingHandler(injector.getInstance(it.handler))
        }
        router.get("/shuffler*").handler(ApplicationPageHandler)
        router.route("$apiRoot/*").failureHandler {
            Logger.error(it.failure(), "Unknown exception")
            it.response().statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            it.response().end()
        }
    }
}