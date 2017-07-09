package it.introini.spotifyplshuffler.routes

import com.google.inject.Inject
import com.google.inject.Injector
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.LoggerHandler
import it.introini.spotifyplshuffler.config.Config


class RouteBinder @Inject constructor(val injector: Injector,
                                      val router: Router,
                                      val config: Config) {

    val API_ROOT = "/shuffler/api/v1"

    fun bindRoutes() {
        val routes = Route.values()
        router.route("$API_ROOT/*").handler(BodyHandler.create())
        router.route("$API_ROOT/*").handler(LoggerHandler.create())
        router.route("$API_ROOT/*").handler(CookieHandler.create())
        routes.forEach {
            router.route(it.method, "$API_ROOT${it.endpoint}").blockingHandler(injector.getInstance(it.handler))
        }
    }
}