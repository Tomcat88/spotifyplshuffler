package it.introini.jscookie


@JsModule("js-cookie")
external open class Cookies {
    companion object: Cookies
    fun get(): String
    fun get(key: String): String?
    fun set(vararg any: Any)
    fun remove(vararg any: Any)
}