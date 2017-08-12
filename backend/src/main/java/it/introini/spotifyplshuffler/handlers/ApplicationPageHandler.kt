package it.introini.spotifyplshuffler.handlers

import io.netty.handler.codec.http.HttpHeaderNames
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import kotlinx.html.*
import kotlinx.html.stream.createHTML


open class ApplicationPageHandler: Handler<RoutingContext> {
    companion object : ApplicationPageHandler()

    override fun handle(event: RoutingContext) {
        event.response().putHeader(HttpHeaderNames.CONTENT_TYPE, "text/html")
        event.response().end(createHTML(true).html {
            head {
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/css?family=Roboto:300,400,500")
            }
            body {
                div { id = "app" }
                script(src = "http://localhost:8083/frontend/frontend.bundle.js")
            }
        })
    }
}